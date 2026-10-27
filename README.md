# home_bot

Бот для управления умным домом. Работает одновременно в **Telegram** и **Максе**
(https://dev.max.ru/docs-api): бизнес-логика (команды, callback-кнопки, рассылки,
шахматные турниры, интеграции) общая для обеих платформ, транспорты независимы.

## Структура

- `com.cheremnov.bot.messenger` — платформо-независимый слой (модели, `Messenger`,
  `MessengerService` с маршрутизацией команд/callback'ов и отправкой по ключу чата).
  Ключи идентификаторов самодостаточны: `tg:123`, `mx:u123` (диалог), `mx:c123` (чат/канал).
- `com.cheremnov.bot.telegram` — транспорт Telegram (long polling telegrambots).
- `com.cheremnov.bot.max` — транспорт Макса (long polling `GET /updates`,
  REST-клиент `platform-api2.max.ru`).
- `com.cheremnov.bot.command` — обработчики команд и кнопок (общие для платформ).

## Конфигурация (application.yml)

| Параметр | Описание |
| --- | --- |
| `token` | токен Telegram-бота (передаётся как `--token=...`) |
| `telegram.enabled` | включить транспорт Telegram (по умолчанию true) |
| `max.enabled` | включить транспорт Макса |
| `max.token` | токен бота Макса |
| `max.base-url` | `https://platform-api2.max.ru` |
| `max.poll-timeout` | тайм-аут long polling, сек |
| `bot.super-admins` | ключи супер-администраторов, например `tg:410563696, mx:123` |

Создание ботов в Максе доступно для юрлиц/ИП/самозанятых — токен выдаётся после
модерации бота на https://business.max.ru/self.

## Примечания

- TLS: сертификат `platform-api2.max.ru` выпущен УЦ Минцифры; корневые сертификаты
  включены в ресурс `src/main/resources/max/russian-trusted-ca.pem` и добавляются
  к системным при создании SSL-контекста (`MaxSsl`).
- Старые данные H2 (числовые id) автоматически мигрируются в строковые ключи
  с префиксом `tg:` при старте (`DataMigration`).
- **Ограничение Макса (проверено 2026-10)**: long polling (`GET /updates`) отдаёт
  медиа-сообщения (голосовые) событием `message_created` **без тела сообщения** —
  контент недоступен никакими дозапросами. Через Webhook (`POST /subscriptions`)
  те же события приходят с полным телом (включая `attachments[].payload.url`).
  То есть для приёма голосовых/медиа нужен Webhook с публичным HTTPS-адресом
  (например, Cloudflare Worker-«почтовый ящик» или cloudflared tunnel).
  В текущем режиме (long polling) текстовые сообщения работают, медиа — пропускаются
  (в лог пишется «Событие message_created без тела сообщения»).