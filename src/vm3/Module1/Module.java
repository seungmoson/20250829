package Module1;

public class Module {
    public static void run() {
        System.out.println("module 1을 실행하겠습니다.");
        System.out.println("module 1은 상속을 테스트 하고 있습니다.");

        Nintendo nintendo =  new Nintendo();
        nintendo.setName("nintendo");
        nintendo.setPrice(600000);

        System.out.println(nintendo.name);
        System.out.println(nintendo.price);
    }


}
