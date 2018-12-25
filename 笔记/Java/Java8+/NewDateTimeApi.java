public class NewDateTimeApi{

  public void baseUse(){
    // 日期基本构造
    LocalDate today = LocalDate.now();
    LocalDate date = LocalDate.of(2014, 3, 18);
    // 基本使用
    int year = date.getYear();
    Month month = date.getMonth();
    int day = date.getDayOfMonth();
    DayOfWeek dow = date.getDayOfWeek();
    int len = date.lengthOfMonth();
    boolean leap = date.isLeapYear();

    int year = date.get(ChronoField.YEAR);
    int month = date.get(ChronoField.MONTH_OF_YEAR);
    int day = date.get(ChronoField.DAY_OF_MONTH);


    // 时间的基本构造
    LocalTime time = LocalTime.of(13, 45, 20);
    int hour = time.getHour();
    int minute = time.getMinute();
    int second = time.getSecond();


    LocalDate date = LocalDate.parse("2014-03-18");
    LocalTime time = LocalTime.parse("13:45:20");

    // 合并
    LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
    LocalDateTime dt2 = LocalDateTime.of(date, time);
    LocalDateTime dt3 = date.atTime(13, 45, 20);
    LocalDateTime dt4 = date.atTime(time);
    LocalDateTime dt5 = time.atDate(date);

    // 提取
    LocalDate date1 = dt1.toLocalDate();
    LocalTime time1 = dt1.toLocalTime();

    int day = Instant.now();


    // 范围描述
    Duration d1 = Duration.between(time1, time2);
    Duration d1 = Duration.between(dateTime1, dateTime2);
    Duration d2 = Duration.between(instant1, instant2);

    Period tenDays = Period.between(LocalDate.of(2014, 3, 8),
                                    LocalDate.of(2014, 3, 18));

    Duration threeMinutes = Duration.ofMinutes(3);
    Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
    Period tenDays = Period.ofDays(10);
    Period threeWeeks = Period.ofWeeks(3);
    Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
  }
  


  public void process(){
    // 修改
    LocalDate date1 = LocalDate.of(2014, 3, 18);  // 2014-3-18
    LocalDate date2 = date1.withYear(2011);  // 2011-3-18
    LocalDate date3 = date2.withDayOfMonth(25);  // 2011-3-25
    LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);  // 2011-9-25

    // 操作
    LocalDate date1 = LocalDate.of(2014, 3, 18);  // 2014-03-18
    LocalDate date2 = date1.plusWeeks(1);  // 2014-03-25
    LocalDate date3 = date2.minusYears(3);  // 2011-03-25
    LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);  // 2011-09-25
    // import static java.time.temporal.TemporalAdjusters.*;
    LocalDate date1 = LocalDate.of(2014, 3, 18);  // 2014-03-18
    LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));  // 2014-03-23
    LocalDate date3 = date2.with(lastDayOfMonth());  // 2014-03-31
  }


  public void format(){
    LocalDate date = LocalDate.of(2014, 3, 18);
    String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);  // 20140318
    String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);  // 2014-03-18

    LocalDate date1 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
    LocalDate date2 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate date1 = LocalDate.of(2014, 3, 18);
    String formattedDate = date1.format(formatter);
    LocalDate date2 = LocalDate.parse(formattedDate, formatter);
  }
}


/**
 * 定制的 TemporalAdjuster
 * 获取下一个工作日，自动跳过周末
 */
public class NextWorkingDay implements TemporalAdjuster {
  @Override
  public Temporal adjustInto(Temporal temporal) {
    // 获取星期
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToAdd = 1;
    // 周五增加 3 天
    if (dow == DayOfWeek.FRIDAY)
      dayToAdd = 3;
    else if (dow == DayOfWeek.SATURDAY) 
      dayToAdd = 2;
    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
  }
}

// Lambda 形式
date = date.with(temporal -> {
  DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
  int dayToAdd = 1;
  if (dow == DayOfWeek.FRIDAY)
    dayToAdd = 3;
  else if (dow == DayOfWeek.SATURDAY) 
    dayToAdd = 2;
  return temporal.plus(dayToAdd, ChronoUnit.DAYS);
});

TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
  temporal -> {
    DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
    int dayToAdd = 1;
    if (dow == DayOfWeek.FRIDAY) 
      dayToAdd = 3;
    if (dow == DayOfWeek.SATURDAY) 
      dayToAdd = 2;
    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
});
date = date.with(nextWorkingDay);








// 自定义 Formatter ，了解
DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
  .appendText(ChronoField.DAY_OF_MONTH)
  .appendLiteral(". ")
  .appendText(ChronoField.MONTH_OF_YEAR)
  .appendLiteral(" ")
  .appendText(ChronoField.YEAR)
  .parseCaseInsensitive()
  .toFormatter(Locale.ITALIAN);