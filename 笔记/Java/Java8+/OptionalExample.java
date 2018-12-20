/**
 * 由于 Optional 类设计时就没特别考虑将其作为类的字段使用，所以它也并未实现 Serializable 接口。
 *
 * 常用方法：
 *    get() 是这些方法中最简单但又最不安全的方法。如果变量存在，它直接返回封装的变量值，否则就抛出一个 NoSuchElementException 异常。
 *    orElse(T other)  不存在时，提供一个默认值；
 *    orElseGet(Supplier<? extends T> other) 是 orElse 方法的延迟调用版， Supplier 方法只有在 Optional 对象不含值时才执行调用（适用于创建值比较费劲的情况）。
 *    orElseThrow(Supplier<? extends X> exceptionSupplier) 和 get 方法非常类似，可以自定义抛出的异常；
 *    ifPresent(Consumer<? super T>) 让你能在变量值存在时执行一个作为参数传入的方法，否则就不进行任何操作。
 */
class OptionalExample{
  public static void main(String[] args) {
    // 声明一个空的 Optional
    Optional<Car> optCar = Optional.empty();

    // 依据一个非空值创建 Optional，如果为空立即抛出异常
    Optional<Car> optCar = Optional.of(car);

    // 可接受 null 的 Optional
    Optional<Car> optCar = Optional.ofNullable(car);

  }


  /**
   * 关于取值
   */
  public getVal(){
    // 使用 map 从 Optional 对象中提取和转换值，避免异常
    Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
    // 如果 Optional 为空，就什么也不做
    Optional<String> name = optInsurance.map(Insurance::getName);


    /**
     * 链式调用时嵌套式 optional 结构问题（使用 flatMap）
     * 实体类中如果使用 Optional 定义属性，那么 getter 方法获取到的就是 Optional 类型
     */
    person.flatMap(Person::getCar)
      .flatMap(Car::getInsurance)
      // getName 返回的是 String 类型
      .map(Insurance::getName)
      .orElse("Unknown");


    // get 配合 isPresent，不过和一般的 null 检查太像了
    if(person.isPresent()){
      return person.get();
    }


    // 类似的三元运算符；
    // person 不存在就不会执行 Lambda（car.map），car 不存在就不会执行 Lambda（test方法）；只要有一个不存在就会返回一个空 Optional
    person.flatMap(p -> car.map(c -> test(p, c)));
  }


  /**
   * 对 Optional 对象进行过滤
   */
  public filter(){
    Optional<Insurance> optInsurance = ...;
    // filter 方法接受一个谓词作为参数。
    // 如果 Optional 对象的值存在，并且它符合谓词的条件，filter 方法就返回其值；否则它就返回一个空的 Optional 对象。
    optInsurance.filter(insurance ->
      "CambridgeInsurance".equals(insurance.getName()))
      .ifPresent(x -> System.out.println("ok"));
  }
  // 一个例子，如果这个人大于限定的年龄，那么返回这个人车的保险公司名字
  public String getCarInsuranceName(Optional<Person> person, int minAge) {
    return person.filter(p -> p.getAge() >= minAge)
      .flatMap(Person::getCar)
      .flatMap(Car::getInsurance)
      .map(Insurance::getName)
      .orElse("Unknown");
  }
}

// 序列化的替代方案
public class Person {
  private Car car;
  public Optional<Car> getCarAsOptional() {
    return Optional.ofNullable(car);
  }
}

// 与 Stream 对象一样， Optional 也提供了类似的基础类型 —— OptionalInt 、 OptionalLong 以及 OptionalDouble
// 但是不推荐使用，因为基本类型的 Optional 会丧失一些好用的方法