package Module4;

public class Module {
    public static void run() {
        System.out.println("module 4을 실행하겠습니다.");
        System.out.println("module 4는 다형성 테스트하고 있습니다.");

        Nintendo nintendo =  new Nintendo();
        XBOX xbox = new XBOX();
        GameMachineInformation gameMachineInformation = new GameMachineInformation();
        GameMachineInformation.guidename(nintendo);

        GameMachineInformation.guideprice(XBOX);


    }


}
