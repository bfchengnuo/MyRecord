# Java设计模式-工厂模式

<br>
<br>

**一、工厂模式主要是为创建对象提供过渡接口，以便将创建对象的具体过程屏蔽隔离起来，达到提高灵活性的目的。**


工厂模式在《Java与模式》中分为三类：

1）简单工厂模式（Simple Factory）：不利于产生系列产品；

2）工厂方法模式（Factory Method）：又称为多形性工厂；

3）抽象工厂模式（Abstract Factory）：又称为工具箱，产生产品族，但不利于产生新的产品；

这三种模式从上到下逐步抽象，并且更具一般性。

GOF在《设计模式》一书中将工厂模式分为两类：工厂方法模式（Factory Method）与抽象工厂模式（Abstract Factory）。将简单工厂模式（Simple Factory）看为工厂方法模式的一种特例，两者归为一类。


## 二、简单工厂模式

简单工厂模式又称静态工厂方法模式。重命名上就可以看出这个模式一定很简单。它存在的目的很简单：**定义一个用于创建对象的接口。**

在简单工厂模式中,一个工厂类处于对产品类实例化调用的中心位置上,它决定那一个产品类应当被实例化, 如同一个交通警察站在来往的车辆流中,决定放行那一个方向的车辆向那一个方向流动一样。

先来看看它的组成：

1) 工厂类角色：这是本模式的核心，含有一定的商业逻辑和判断逻辑。在java中它往往由一个具体类实现。

2) 抽象产品角色：它一般是具体产品继承的父类或者实现的接口。在java中由接口或者抽象类来实现。

3) 具体产品角色：工厂类所创建的对象就是此角色的实例。在java中由一个具体类实现。

- [示例](http://www.cnblogs.com/java-my-life/archive/2012/03/22/2412308.html)

## 三、工厂方法模式

工厂方法模式是简单工厂模式的进一步抽象化和推广，工厂方法模式里不再只由一个工厂类决定那一个产品类应当被实例化,这个决定被交给抽象工厂的子类去做。

来看下它的组成：

1) 抽象工厂角色： 这是工厂方法模式的核心，它与应用程序无关。是具体工厂角色必须实现的接口或者必须继承的父类。在java中它由抽象类或者接口来实现。

2) 具体工厂角色：它含有和具体业务逻辑有关的代码。由应用程序调用以创建对应的具体产品的对象。

3) 抽象产品角色：它是具体产品继承的父类或者是实现的接口。在java中一般有抽象类或者接口来实现。

4) 具体产品角色：具体工厂角色所创建的对象就是此角色的实例。在java中由具体的类来实现。


工厂方法模式使用继承自抽象工厂角色的多个子类来代替简单工厂模式中的“上帝类”。正如上面所说，这样便分担了对象承受的压力；而且这样使得结构变得灵活 起来——当有新的产品（即暴发户的汽车）产生时，只要按照抽象产品角色、抽象工厂角色提供的合同来生成，那么就可以被客户使用，而不必去修改任何已有的代 码。可以看出工厂角色的结构也是符合开闭原则的！

代码：

	//抽象产品角色
	public interface Moveable {
	    void run();
	}
	//具体产品角色
	public class Plane implements Moveable {
	    @Override
	    public void run() {
	        System.out.println("plane....");
	    }
	}
	
	public class Broom implements Moveable {
	    @Override
	    public void run() {
	        System.out.println("broom.....");
	    }
	}
	
	//抽象工厂
	public abstract class VehicleFactory {
	    abstract Moveable create();
	}
	//具体工厂
	public class PlaneFactory extends VehicleFactory{
	    public Moveable create() {
	        return new Plane();
	    }
	}
	public class BroomFactory extends VehicleFactory{
	    public Moveable create() {
	        return new Broom();
	    }
	}
	//测试类
	public class Test {
	    public static void main(String[] args) {
	        VehicleFactory factory = new BroomFactory();
	        Moveable m = factory.create();
	        m.run();
	    }
	}


可以看出工厂方法的加入，使得对象的数量成倍增长。当产品种类非常多时，会出现大量的与之对应的工厂对象，这不是我们所希望的。因为如果不能避免这种情 况，可以考虑使用简单工厂模式与工厂方法模式相结合的方式来减少工厂类：即对于产品树上类似的种类（一般是树的叶子中互为兄弟的）使用简单工厂模式来实 现。

## 四、简单工厂和工厂方法模式的比较

工厂方法模式和简单工厂模式在定义上的不同是很明显的。工厂方法模式的核心是一个抽象工厂类,而不像简单工厂模式, 把核心放在一个实类上。工厂方法模式可以允许很多实的工厂类从抽象工厂类继承下来, 从而可以在实际上成为多个简单工厂模式的综合,从而推广了简单工厂模式。 

反过来讲,简单工厂模式是由工厂方法模式退化而来。设想如果我们非常确定一个系统只需要一个实的工厂类, 那么就不妨把抽象工厂类合并到实的工厂类中去。而这样一来,我们就退化到简单工厂模式了。

## 五、抽象工厂模式

代码：

	//抽象工厂类
	public abstract class AbstractFactory {
	    public abstract Vehicle createVehicle();
	    public abstract Weapon createWeapon();
	    public abstract Food createFood();
	}
	//具体工厂类，其中Food,Vehicle，Weapon是抽象类，
	public class DefaultFactory extends AbstractFactory{
	    @Override
	    public Food createFood() {
	        return new Apple();
	    }
	    @Override
	    public Vehicle createVehicle() {
	        return new Car();
	    }
	    @Override
	    public Weapon createWeapon() {
	        return new AK47();
	    }
	}
	//测试类
	public class Test {
	    public static void main(String[] args) {
	        AbstractFactory f = new DefaultFactory();
	        Vehicle v = f.createVehicle();
	        v.run();
	        Weapon w = f.createWeapon();
	        w.shoot();
	        Food a = f.createFood();
	        a.printName();
	    }
	}
	
在抽象工厂模式中，抽象产品 (AbstractProduct) 可能是一个或多个，从而构成一个或多个产品族(Product Family)。 在只有一个产品族的情况下，抽象工厂模式实际上退化到工厂方法模式。

## 六、总结。

（1）简单工厂模式是由一个具体的类去创建其他类的实例，父类是相同的，父类是具体的。 

（2）工厂方法模式是有一个抽象的父类定义公共接口，子类负责生成具体的对象，这样做的目的是将类的实例化操作延迟到子类中完成。 

（3）抽象工厂模式提供一个创建一系列相关或相互依赖对象的接口，而无须指定他们具体的类。它针对的是有多个产品的等级结构。而工厂方法模式针对的是一个产品的等级结构。

<br>

[原文连接](http://www.cnblogs.com/forlina/archive/2011/06/21/2086114.html)