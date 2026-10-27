/**
 * Telegram Bot API relay on Cloudflare Workers.
 *
 * Формат запроса:
 *   https://<worker>.workers.dev/<SECRET>/bot<TOKEN>/<method>
 * проксируется на
 *   https://api.telegram.org/bot<TOKEN>/<method>
 *
 * <SECRET> — общий секрет (заголовок-путь), задаётся переменной окружения
 * RELAY_SECRET в настройках Worker. Запросы без секрета/с неверным — 403.
 *
 * Заголовок X-Relay-Secret тоже принимается (если задан) — на случай клиентов,
 * которые умеют добавлять заголовки.
 */

const UPSTREAM = "https://api.telegram.org";

export default {
  async fetch(request, env) {
    const secret = env.RELAY_SECRET;
    if (!secret) {
      return new Response("relay not configured", { status: 500 });
    }

    const url = new URL(request.url);
    const parts = url.pathname.split("/").filter((p) => p.length > 0);

    // Ожидаем: <SECRET>/bot<TOKEN>/<method...>
    const headerSecret = request.headers.get("X-Relay-Secret");
    let rest;
    if (headerSecret && timingSafeEqual(headerSecret, secret)) {
      rest = parts; // секрет пришёл заголовком, путь начинается с /bot...
    } else if (parts.length >= 2 && timingSafeEqual(parts[0], secret)) {
      rest = parts.slice(1);
    } else {
      return new Response("forbidden", { status: 403 });
    }

    if (rest.length < 2 || !rest[0].startsWith("bot")) {
      return new Response("not found", { status: 404 });
    }

    const target = UPSTREAM + "/" + rest.join("/") + url.search;

    const headers = new Headers();
    for (const [k, v] of request.headers) {
      const lk = k.toLowerCase();
      if (lk === "host" || lk === "x-relay-secret" || lk === "cf-connecting-ip") {
        continue;
      }
      headers.set(k, v);
    }

    const init = {
      method: request.method,
      headers,
      body:
        request.method === "GET" || request.method === "HEAD"
          ? undefined
          : request.body,
      redirect: "manual",
    };

    try {
      const resp = await fetch(target, init);
      const outHeaders = new Headers(resp.headers);
      outHeaders.delete("content-encoding");
      outHeaders.delete("content-length");
      return new Response(resp.body, {
        status: resp.status,
        statusText: resp.statusText,
        headers: outHeaders,
      });
    } catch (e) {
      return new Response("upstream error: " + e.message, { status: 502 });
    }
  },
};

function timingSafeEqual(a, b) {
  if (a.length !== b.length) {
    return false;
  }
  let res = 0;
  for (let i = 0; i < a.length; i++) {
    res |= a.charCodeAt(i) ^ b.charCodeAt(i);
  }
  return res === 0;
}