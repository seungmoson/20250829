package vm1;

public class ZooManager {

    public void manageFood(Animal animal) {
        if (animal != null) {
            System.out.println("먹이 주기 로직을 실행합니다.");
            animal.giveFood();
        } else {
            System.out.println("유효한 동물이 선택되지 않아 작업을 종료합니다.");
        }
    }

    public static void main(String[] args) {
        AnimalSelector selector = new AnimalSelector();
        Animal selectedAnimal = selector.chooseAnimal();
        ZooManager manager = new ZooManager();
        manager.manageFood(selectedAnimal);
    }
}