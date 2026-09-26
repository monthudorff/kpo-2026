# Практика 2: подсказки по реализации на Java

Этот документ помогает реализовать практику на Java 25. Полное решение здесь намеренно не приводится: самостоятельно реализуйте конкретные сохранители, отправители и проверки ошибок.

Общее языконезависимое задание находится в [README.md](README.md).

## Рекомендуемая структура

Разделите варианты, чтобы монолитная реализация не смешивалась с вариантом по SOLID:

```text
studying/
├── withoutsolid/
│   ├── Main.java
│   └── ReportService.java
└── withsolid/
    ├── Main.java
    ├── model/
    │   └── Report.java
    ├── service/
    │   ├── ReportSaver.java
    │   ├── ReportSender.java
    │   └── ReportService.java
    ├── service/impl/
    │   ├── TextReportSaver.java
    │   └── EmailReportSender.java
    └── exception/
        ├── ApplicationErrorCode.java
        └── ApplicationException.java
```

## Вариант без SOLID

В `withoutsolid.ReportService` намеренно объедините три ответственности:

1. Формирование строкового представления отчёта.
2. Запись строки в файл через `Files.writeString`.
3. Вывод сообщения об email-отправке в консоль.

Так станет заметно, почему добавление второго формата файла или нового канала отправки потребует изменения одного и того же класса.

## Модель отчёта

Для данных отчёта удобно применить `record` и Lombok `@Builder`:

```java
@Builder
public record Report(
        String title,
        LocalDate date,
        LocalTime time,
        int carsSold,
        int motorcyclesSold
) {
}
```

Добавьте `toString()` или отдельный метод форматирования, чтобы сформировать текст отчёта. Модель не должна знать о `Path`, файловой системе, email или консоли.

## Контракты варианта с SOLID

Сначала определите маленькие интерфейсы:

```java
@FunctionalInterface
public interface ReportSaver {
    void save(Report report);
}
```

```java
@FunctionalInterface
public interface ReportSender {
    void send(Report report, String email);
}
```

`ReportService` хранит эти контракты в `final` полях. Для конструктора можно применить Lombok:

```java
@RequiredArgsConstructor
public final class ReportService {
    private final ReportSaver reportSaver;
    private final ReportSender reportSender;

    public void process(Report report, String email) {
        // Сначала сохраните report, затем отправьте его на email.
    }
}
```

Не создавайте `TextReportSaver` или `EmailReportSender` внутри `ReportService`: конкретные реализации нужно передать в `Main` при создании сервиса.

## Конкретные реализации

### Сохранение в файл

`TextReportSaver` реализует `ReportSaver`. Путь можно сформировать динамически:

```text
reports/report-<дата>-<время>.txt
```

Для этого понадобятся `Path.of`, `Files.createDirectories`, `Files.writeString` и `DateTimeFormatter`. Ошибку `IOException` следует обернуть в прикладное runtime-исключение, сохранив исходную ошибку как `cause`.

### Email-отправка

`EmailReportSender` реализует `ReportSender`. Реальную интеграцию с SMTP делать не нужно: достаточно `System.out.printf`, в котором выводятся заголовок отчёта и email получателя.

## Прикладные ошибки

Создайте enum с кодами, например:

```java
public enum ApplicationErrorCode {
    VALIDATION_ERROR,
    FILE_WRITE_ERROR
}
```

Создайте собственное исключение, наследуемое от `RuntimeException`. Оно должно хранить код ошибки и поддерживать передачу `message` и `cause` в родительский конструктор. Lombok `@Getter` подойдёт для поля с кодом.

Проверьте как минимум:

- `report` не равен `null` при сохранении и отправке;
- `email` не равен `null` при отправке;
- показатели в отчёте не отрицательные, если вы добавляете эту валидацию;
- ошибка записи файла получает код `FILE_WRITE_ERROR` и исходный `IOException` как причину.

## Тесты JUnit 5

Используйте `@DisplayName` с русским описанием сценария. Полезные тесты:

- `ReportService` вызывает сохранитель до отправителя; для проверки подойдут лямбда-реализации интерфейсов и список вызовов.
- `TextReportSaver` создаёт файл с ожидаемым содержимым; используйте `@TempDir`, если реализация допускает передачу каталога для теста.
- `EmailReportSender` выводит адрес и заголовок; при необходимости перехватите `System.out`.
- Ошибочные входные данные приводят к вашему `ApplicationException` с ожидаемым кодом.

Запуск тестов из корня проекта:

```bash
./gradlew :modules:practise-2:test
```

## Что проверить перед сдачей

- [ ] `withsolid` не зависит от пакета `withoutsolid`.
- [ ] В `ReportService` нет `new TextReportSaver()` и `new EmailReportSender()`.
- [ ] Новая реализация `ReportSaver` или `ReportSender` подключается без изменения `ReportService`.
- [ ] Для публичных методов добавлен Javadoc.
- [ ] Генерируемые отчёты исключены из Git через `.gitignore`.
