package Module2;

public class Module {
    public static void run() {
        System.out.println("module 2를 실행하겠습니다.");
        System.out.println("module 2는 생성자를 테스트 하고 있습니다.");

        Nintendo nintendo =  new Nintendo("nintendo", 600002);
//        nintendo.setName("nintendo");
//        nintendo.setPrice(600000);

        System.out.println(nintendo.name);
        System.out.println(nintendo.price);
    }


}
