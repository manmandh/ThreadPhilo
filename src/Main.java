import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class DiningPhilosophers {
    public static void main(String[] args) throws Exception {
        int numberOfPhilosophers = 5;
        //khai báo số lượng triết gia
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Chopstick[] chopsticks = new Chopstick[numberOfPhilosophers];
        //tạo mảng cho các triết gia và đũa(và cùng bằng số lượng triết gia)
        for (int i = 0; i < numberOfPhilosophers; i++) {
            chopsticks[i] = new Chopstick();
        }
        //tạo mảng các object Chopstick tương ứng với số lượng triết gia,
        //mỗi cái choptick tương ứng vs 1 chiếc đũa
        for (int i = 0; i < numberOfPhilosophers; i++) {
            philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % numberOfPhilosophers]);
            //tạo mảng các object Philosopher tương ứng vơí đũa:
            //+chopsticks[i] là đũa trái
            //+chopsticks[(i + 1) % numberOfPhilosophers]: là đũa bên phải
            philosophers[i].start();
            //gọi start để chạy luồng
        }
    }
}

class Philosopher extends Thread {
    private int id;
    private Chopstick leftChopstick;
    private Chopstick rightChopstick;

    public Philosopher(int id, Chopstick leftChopstick, Chopstick rightChopstick) {
        this.id = id;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }
    //truyền tham số đối tượng, đũa trái, phải
    //Mỗi cái thread sẽ chạy những luồng khác nhau, riêng biệt.

    public void run() {
        //run thực hiện thông qua start dòng 20
        try {
            while (true) {
                //vòng lặp vô hạn
                think();
                eat();
            }
            //có 2 hành động đang xảy ra là suy nghĩ với ăn
        } catch (InterruptedException e) {
            //xử lí ngoại lệ
            Thread.currentThread().interrupt();
            //để thiết lập interrupt cho luồng và kết thúc vòng lặp hiện tại
            //sẽ bị gián đoạn nếu nó xảy ra
        }
    }

    private void think() throws InterruptedException {
        //dòng 47: sẽ ngắt lệnh nếu có lỗi xayra
        System.out.println("Philosopher " + id + " is thinking...");
        //suy nghĩ trước khi ăn
        Thread.sleep((long) (Math.random() * 20000));
        //sleep thì để tạm dừng trong khoảng thời gian  mà mình suy nghĩ trước khi ăn
        //chẳng hạn như 20 seconds
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is so hungry and trying to pick up chopsticks...");
        //tương tự với nghĩ thì triết gia cũng đang đói và cố cầm  đũa lên

        //sử dụng ReentrantLock để đảm bảo đồng bộ hóa:
        /*
        Có thể GOOGLE tham khảo thêm
        phân biệt đc synchronized với Reentranlock
        ReentrantLock là một lớp trong package java.util.concurrent.locks được sử dụng để cung cấp
        khả năng đồng bộ hóa tốt hơn cho các luồng (threads) trong môi trường đa luồng (multithreaded environment)
        của Java. Nó là một loại khóa (lock) cho phép các luồng có thể thực hiện khóa và giải khóa một cách tuần tự.
        Với ReentrantLock, một luồng có thể có quyền khóa nhiều lần, đồng nghĩa với việc nó có thể có quyền mở khóa
        nhiều lần và giữ khóa cho đến khi tất cả các luồng khác đã hoàn tất thao tác của mình. Nó cũng cho phép khả
        năng giải phóng khóa từ bất kỳ luồng nào trong các luồng đang chờ đợi.ReentrantLock cung cấp một số lợi ích
        so với việc sử dụng khóa truyền thống bằng từ khóa synchronized của Java, bao gồm khả năng chặn không có thời
        gian (non-blocking) và khả năng thực hiện khóa và mở khóa trong cùng một phương thức. Tuy nhiên, ReentrantLock
        cũng có một số hạn chế,chẳng hạn như khó sử dụng hơn so với synchronized và yêu cầu thực hiện đúng các thủ tục
        giải phóng khóa để tránh các lỗi liên quan đến deadlock (mắc kẹt).*/
        Lock leftLock = leftChopstick.getLock();
        //Triết gia sẽ lấy đũa bên trái
        Lock rightLock = rightChopstick.getLock();
        //Triết gia sẽ lấy đũa bên phải
        leftLock.lock();
        //lock sẽ tương ứng với từng đũa
        //triết gia sẽ cố gắng giữ đũa bên trái trước
        try {
            rightLock.lock();
            //sau đấy cố giữ cái đũa bên phải
            try {
                System.out.println("Philosopher " + id + " is eating...");
                //ăn ngẫu nhiên
                Thread.sleep((long) (Math.random() * 10000));
            } finally {
                rightLock.unlock();
                //sau đấy giải phóng từng đũa, phải trước
            }
        } finally {
            leftLock.unlock();
            //sau đó là trái
        }
        //sử dụng try lock để tránh mắc kẹt
    }
}

class Chopstick {

    // Lock tím này để chặn việc truy cập nếu nó đc sử dụng ở triết gia khác
    private Lock lock = new ReentrantLock();
    //tạo cái objeck lock cho ReentrantLock
    //tại vì để đồng bộ hóa cái đũa mình ăn thì phải gọi lại bên choptick này để giữ đồng bộ hóa

    public Lock getLock() {
        return lock;
        //trả về cái đối tượng lock kia trong quá trình nó đồng bộ hóa
    }
}