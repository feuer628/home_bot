# Cloudflare Worker — релей к Telegram Bot API

Обход блокировки `api.telegram.org` через бесплатный Cloudflare Worker.
Из проверки сети (`tg-proxy --check`) видно, что Cloudflare-адреса
доступны, а прямые адреса Telegram — нет.

## Как это работает

```
Spring-бот  →  https://<worker>.workers.dev/<SECRET>/bot<TOKEN>/<method>
                        ↓
               api.telegram.org/bot<TOKEN>/<method>
```

Worker пересылает запрос как есть (метод, заголовки, тело — включая
multipart для файлов), поэтому поддерживается и long-polling (getUpdates).

## Шаг 1. Аккаунт Cloudflare

1. Регистрация: https://dash.cloudflare.com/sign-up (бесплатно).
2. Подтвердить e-mail.

## Шаг 2. Деплой Worker

### Вариант A — через wrangler (CLI, быстрее)

Нужен Node.js (v18+). Из папки `cloudflare-worker`:

```powershell
npx wrangler login                                            # откроет браузер, авторизуйтесь
-join ((48..57) + (97..122) | Get-Random -Count 40 | ForEach-Object { [char]$_ }) > secret.txt
npx wrangler secret put RELAY_SECRET                          # вставьте значение из secret.txt
npx wrangler deploy                                           # выведет URL воркера
```

`wrangler login` пишет креды в `%USERPROFILE%\.wrangler` — не коммитить их.

### Вариант B — через дашборд

1. В дашборде: **Workers & Pages** → **Create** → **Create Worker**.
2. Имя, например `tg-bot-relay`. Нажать **Deploy**.
3. **Edit code** → удалить шаблон, вставить содержимое `worker.js` → **Deploy**.
4. **Settings** → **Variables and Secrets** → **Add** → тип **Secret**,
   имя `RELAY_SECRET`, значение — случайная строка → Save.

Одноразовая генерация секрета (PowerShell):

```powershell
-join ((48..57) + (97..122) | Get-Random -Count 40 | ForEach-Object { [char]$_ })
```

## Шаг 4. Проверить Worker

Замените `<worker>` и `<SECRET>` и подставьте токен бота:

```powershell
curl.exe "https://<worker>.<account>.workers.dev/<SECRET>/bot<TOKEN>/getMe"
```

Ожидаемый ответ — JSON вида `{"ok":true,"result":{...}}`.
Без секрета должен вернуться `403`.

## Шаг 5. Настроить бота

В `application.yml`:

```yaml
telegram:
  api:
    base-url: https://<worker>.<account>.workers.dev
    secret: <SECRET>
  proxy:
    enabled: false
    host:
    port:
    user:
    password:
```

Либо аргументами запуска (не коммитить секрет в репозиторий):

```
--telegram.api.base-url=https://<worker>.<account>.workers.dev
--telegram.api.secret=<SECRET>
```

## Замечания по безопасности

- `RELAY_SECRET` держите в секрете: кто знает URL с секретом — сможет
  ходить к вашему Worker (но не получит доступ к боту без токена).
- Токен бота всё равно уходит в Telegram; Worker его видит транзитом —
  это ваш собственный Worker, доверие приемлемое.
- Ограничить Worker можно правилами CF (Rate limiting, WAF) при желании.
- Лимит бесплатного тарифа: 100 000 запросов/день — для бота с
  long-polling достаточно (getUpdates держится ~30-50 секунд на запрос).