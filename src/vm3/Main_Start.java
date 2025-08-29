public class Main_Start {
    public static void main(String[] args) {

        int moduleNumber = 3;
        switch (moduleNumber) {
            case 1: ;
                Module1.Module.run();
                break;
            case 2:
                Module2.Module.run();
                break;
            case 3:
                Module3.Module.run();
                break;
//            case 4:
//                Module4.Module.run();
//                break;

            default: System.out.println("Error");
                break;
        }

    System.out.println("끝났습니다.");
    }
}
