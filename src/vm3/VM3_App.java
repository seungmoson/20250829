package vm3;

import java.util.*;

//인터페이스 추상클래스 상속
public class VM3_App {

    // =========================
    // 인터페이스: 제품 최소 스펙
    // =========================
    interface VM3_IProduct {
        String VM3_getName();
        int VM3_getPrice();
        int VM3_getStock();
        void VM3_addStock(int VM3_amount);
        void VM3_setStock(int VM3_amount);
    }

    // =========================
    // 공용 제품 구현체
    // =========================
    static class VM3_Product implements VM3_IProduct {
        String VM3_name;
        int VM3_price;
        int VM3_stock;

        VM3_Product(String VM3_name, int VM3_price, int VM3_stock) {
            this.VM3_name = VM3_name;
            this.VM3_price = VM3_price;
            this.VM3_stock = VM3_stock;
        }

        @Override public String VM3_getName()  { return VM3_name; }
        @Override public int    VM3_getPrice() { return VM3_price; }
        @Override public int    VM3_getStock() { return VM3_stock; }
        @Override public void   VM3_addStock(int VM3_amount) { VM3_stock += VM3_amount; }
        @Override public void   VM3_setStock(int VM3_amount) { VM3_stock = VM3_amount; }
    }

    // =========================
    // 추상 루트 자판기
    // =========================
    static abstract class VM3_VendingMachine {
        protected final String VM3_machineName;
        protected final List<VM3_IProduct> VM3_products = new ArrayList<>();
        protected int VM3_balance = 0;   // 투입 잔고
        protected int VM3_cashbox = 0;   // 금고(매출 누적)
        protected final int VM3_MAX_PRODUCTS = 100;

        protected VM3_VendingMachine(String VM3_machineName) {
            this.VM3_machineName = VM3_machineName;
            VM3_initSamples(); // 각 자식에서 기본 상품 세팅
        }

        // 자식 클래스가 기본 상품을 채움
        protected abstract void VM3_initSamples();

        // (훅) 구매 전 제약 검증(필요 시 자식이 오버라이드)
        protected boolean VM3_validatePurchase(VM3_IProduct VM3_p, Scanner VM3_sc) {
            return true; // 기본 제약 없음
        }

        // (훅) 구매 직전/직후 부가기능
        protected void VM3_beforeDispense(VM3_IProduct VM3_p, Scanner VM3_sc) {}
        protected void VM3_afterDispense (VM3_IProduct VM3_p, Scanner VM3_sc) {}

        // 메인 루프
        public final void VM3_run(Scanner VM3_sc) {
            while (true) {
                System.out.println("\n[" + VM3_machineName + "]");
                System.out.println("1) 판매 모드");
                System.out.println("2) 제품 등록");
                System.out.println("3) 제품 목록 관리(수정/삭제/조회)");
                System.out.println("4) 입출금 관리(투입/거스름돈/정산)");
                System.out.println("0) 이전 메뉴");
                System.out.print("선택: ");
                String VM3_sel = VM3_sc.nextLine().trim();

                switch (VM3_sel) {
                    case "1": VM3_saleMode(VM3_sc); break;
                    case "2": VM3_addProduct(VM3_sc); break;
                    case "3": VM3_manageProducts(VM3_sc); break;
                    case "4": VM3_moneyMenu(VM3_sc); break;
                    case "0": return;
                    default : System.out.println("잘못된 입력입니다.");
                }
            }
        }

        // 판매 모드
        protected void VM3_saleMode(Scanner VM3_sc) {
            while (true) {
                System.out.println("\n[판매 모드] 현재 잔고: " + VM3_balance + "원");
                VM3_printSimpleList();
                System.out.println("a) 금액 투입");
                System.out.println("r) 거스름돈 반환(잔고 비움)");
                System.out.println("0) 이전 메뉴");
                System.out.print("구매할 제품 번호를 선택하거나 메뉴 선택: ");
                String VM3_in = VM3_sc.nextLine().trim();

                if (VM3_in.equals("0")) return;
                if (VM3_in.equalsIgnoreCase("a")) { VM3_deposit(VM3_sc); continue; }
                if (VM3_in.equalsIgnoreCase("r")) { VM3_refund(); continue; }

                try {
                    int VM3_idx = Integer.parseInt(VM3_in);
                    if (VM3_idx < 1 || VM3_idx > VM3_products.size()) {
                        System.out.println("번호 범위를 확인하세요.");
                        continue;
                    }
                    VM3_buy(VM3_idx - 1, VM3_sc);
                } catch (NumberFormatException e) {
                    System.out.println("숫자 또는 메뉴 문자를 입력하세요.");
                }
            }
        }

        protected void VM3_buy(int VM3_idx, Scanner VM3_sc) {
            VM3_IProduct VM3_p = VM3_products.get(VM3_idx);
            if (VM3_p.VM3_getStock() <= 0) {
                System.out.println("재고가 없습니다.");
                return;
            }
            if (VM3_balance < VM3_p.VM3_getPrice()) {
                System.out.println("잔고가 부족합니다. 금액을 더 투입하세요.");
                return;
            }
            // 자식 제약
            if (!VM3_validatePurchase(VM3_p, VM3_sc)) {
                System.out.println("구매 요건을 충족하지 못했습니다.");
                return;
            }

            VM3_beforeDispense(VM3_p, VM3_sc);

            // 결제/지급
            VM3_p.VM3_setStock(VM3_p.VM3_getStock() - 1);
            VM3_balance -= VM3_p.VM3_getPrice();
            VM3_cashbox += VM3_p.VM3_getPrice();
            System.out.println("구매 완료: " + VM3_p.VM3_getName() +
                    " (-1). 남은 잔고: " + VM3_balance + "원");

            VM3_afterDispense(VM3_p, VM3_sc);
        }

        // 제품 등록
        protected void VM3_addProduct(Scanner VM3_sc) {
            if (VM3_products.size() >= VM3_MAX_PRODUCTS) {
                System.out.println("제품 등록 한도에 도달했습니다.");
                return;
            }
            System.out.println("\n[제품 등록]");
            System.out.print("이름: ");
            String VM3_name = VM3_sc.nextLine().trim();
            if (VM3_name.isEmpty()) { System.out.println("이름은 비어 있을 수 없습니다."); return; }
            if (VM3_findByName(VM3_name) != -1) {
                System.out.println("이미 같은 이름의 제품이 있습니다.");
                return;
            }
            int VM3_price = VM3_inputPositiveInt(VM3_sc, "가격(원): ");
            int VM3_stock = VM3_inputNonNegativeInt(VM3_sc, "재고(개): ");

            VM3_products.add(new VM3_Product(VM3_name, VM3_price, VM3_stock));
            System.out.println("등록 완료.");
        }

        // 제품 목록 관리
        protected void VM3_manageProducts(Scanner VM3_sc) {
            while (true) {
                System.out.println("\n[제품 목록 관리]");
                VM3_printFullList();
                System.out.println("1) 가격 수정");
                System.out.println("2) 재고 수정(덧셈/설정)");
                System.out.println("3) 제품 삭제");
                System.out.println("0) 이전 메뉴");
                System.out.print("선택: ");
                String VM3_sel = VM3_sc.nextLine().trim();

                switch (VM3_sel) {
                    case "1": {
                        int VM3_idx1 = VM3_selectIndex(VM3_sc);
                        if (VM3_idx1 == -1) break;
                        int VM3_newPrice = VM3_inputPositiveInt(VM3_sc, "새 가격(원): ");
                        // 공용 구현체 접근(간단화 목적)
                        VM3_Product prod = (VM3_Product) VM3_products.get(VM3_idx1);
                        prod.VM3_price = VM3_newPrice;
                        System.out.println("가격 수정 완료.");
                        break;
                    }
                    case "2": {
                        int VM3_idx2 = VM3_selectIndex(VM3_sc);
                        if (VM3_idx2 == -1) break;
                        System.out.print("모드 선택 (+ 덧셈 / = 설정): ");
                        String VM3_mode = VM3_sc.nextLine().trim();
                        VM3_IProduct p = VM3_products.get(VM3_idx2);
                        if (VM3_mode.equals("+")) {
                            int VM3_add = VM3_inputNonNegativeInt(VM3_sc, "추가할 수량: ");
                            p.VM3_addStock(VM3_add);
                            System.out.println("재고 추가 완료.");
                        } else if (VM3_mode.equals("=")) {
                            int VM3_set = VM3_inputNonNegativeInt(VM3_sc, "설정할 수량: ");
                            p.VM3_setStock(VM3_set);
                            System.out.println("재고 설정 완료.");
                        } else {
                            System.out.println("잘못된 모드입니다.");
                        }
                        break;
                    }
                    case "3": {
                        int VM3_idx3 = VM3_selectIndex(VM3_sc);
                        if (VM3_idx3 == -1) break;
                        VM3_IProduct removed = VM3_products.remove(VM3_idx3);
                        System.out.println("삭제됨: " + removed.VM3_getName());
                        break;
                    }
                    case "0": return;
                    default : System.out.println("잘못된 입력입니다.");
                }
            }
        }

        // 입출금
        protected void VM3_moneyMenu(Scanner VM3_sc) {
            while (true) {
                System.out.println("\n[입출금 관리]");
                System.out.println("현재 잔고: " + VM3_balance + "원, 자판기 매출(금고): " + VM3_cashbox + "원");
                System.out.println("1) 금액 투입");
                System.out.println("2) 거스름돈 반환(잔고 비움)");
                System.out.println("3) 매출 정산(금고를 0으로)");
                System.out.println("0) 이전 메뉴");
                System.out.print("선택: ");
                String VM3_sel = VM3_sc.nextLine().trim();

                switch (VM3_sel) {
                    case "1": VM3_deposit(VM3_sc); break;
                    case "2": VM3_refund(); break;
                    case "3":
                        System.out.println("정산 완료. " + VM3_cashbox + "원을 출금하여 0으로 초기화합니다.");
                        VM3_cashbox = 0;
                        break;
                    case "0": return;
                    default : System.out.println("잘못된 입력입니다.");
                }
            }
        }

        // 공용 유틸
        protected void VM3_deposit(Scanner VM3_sc) {
            int VM3_amount = VM3_inputPositiveInt(VM3_sc, "투입할 금액(원): ");
            VM3_balance += VM3_amount;
            System.out.println("투입 완료. 현재 잔고: " + VM3_balance + "원");
        }

        protected void VM3_refund() {
            if (VM3_balance <= 0) {
                System.out.println("반환할 잔고가 없습니다.");
                return;
            }
            System.out.println("거스름돈 반환: " + VM3_balance + "원");
            VM3_balance = 0;
        }

        protected void VM3_printSimpleList() {
            if (VM3_products.isEmpty()) {
                System.out.println("(등록된 제품이 없습니다.)");
                return;
            }
            System.out.println("번호 | 이름 | 가격 | 재고");
            for (int VM3_i = 0; VM3_i < VM3_products.size(); VM3_i++) {
                VM3_IProduct p = VM3_products.get(VM3_i);
                System.out.printf("%d) %s | %d원 | %d개%n",
                        VM3_i + 1, p.VM3_getName(), p.VM3_getPrice(), p.VM3_getStock());
            }
        }

        protected void VM3_printFullList() {
            System.out.println("=== 제품 목록 ===");
            VM3_printSimpleList();
        }

        protected int VM3_selectIndex(Scanner VM3_sc) {
            if (VM3_products.isEmpty()) {
                System.out.println("제품이 없습니다.");
                return -1;
            }
            VM3_printSimpleList();
            System.out.print("대상 제품 번호: ");
            try {
                int VM3_idx = Integer.parseInt(VM3_sc.nextLine().trim()) - 1;
                if (VM3_idx < 0 || VM3_idx >= VM3_products.size()) {
                    System.out.println("번호 범위를 확인하세요.");
                    return -1;
                }
                return VM3_idx;
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
                return -1;
            }
        }

        protected int VM3_findByName(String VM3_name) {
            for (int i = 0; i < VM3_products.size(); i++) {
                if (VM3_products.get(i).VM3_getName().equalsIgnoreCase(VM3_name)) return i;
            }
            return -1;
        }

        protected int VM3_inputPositiveInt(Scanner VM3_sc, String VM3_msg) {
            while (true) {
                System.out.print(VM3_msg);
                String s = VM3_sc.nextLine().trim();
                try {
                    int v = Integer.parseInt(s);
                    if (v > 0) return v;
                    System.out.println("0보다 큰 값을 입력하세요.");
                } catch (NumberFormatException e) {
                    System.out.println("정수를 입력하세요.");
                }
            }
        }

        protected int VM3_inputNonNegativeInt(Scanner VM3_sc, String VM3_msg) {
            while (true) {
                System.out.print(VM3_msg);
                String s = VM3_sc.nextLine().trim();
                try {
                    int v = Integer.parseInt(s);
                    if (v >= 0) return v;
                    System.out.println("0 이상 값을 입력하세요.");
                } catch (NumberFormatException e) {
                    System.out.println("정수를 입력하세요.");
                }
            }
        }
    }

    // =========================
    // 구체: 음료 자판기
    // =========================
    static class VM3_BeverageMachine extends VM3_VendingMachine {
        VM3_BeverageMachine() { super("VM3 음료 자판기"); }

        @Override
        protected void VM3_initSamples() {
            if (VM3_products.isEmpty()) {
                VM3_products.add(new VM3_Product("콜라",   1500, 5));
                VM3_products.add(new VM3_Product("사이다", 1500, 5));
                VM3_products.add(new VM3_Product("이온음료", 1800, 3));
            }
        }
    }

    // =========================
    // 구체: 라면 자판기 (옵션 데모)
    // =========================
    static class VM3_RamenMachine extends VM3_VendingMachine {
        VM3_RamenMachine() { super("VM3 라면 자판기"); }

        @Override
        protected void VM3_initSamples() {
            if (VM3_products.isEmpty()) {
                VM3_products.add(new VM3_Product("컵라면(매운맛)", 2000, 6));
                VM3_products.add(new VM3_Product("컵라면(순한맛)", 1800, 6));
            }
        }

        @Override
        protected void VM3_afterDispense(VM3_IProduct VM3_p, Scanner VM3_sc) {
            System.out.println("뜨거운 물 제공구에서 물을 받아가세요. (일회용 포크/젓가락 제공)");
        }
    }

    // =========================
    // 구체: 담배 자판기 (성인 인증)
    // =========================
    static class VM3_CigaretteMachine extends VM3_VendingMachine {
        VM3_CigaretteMachine() { super("VM3 담배 자판기"); }

        @Override
        protected void VM3_initSamples() {
            if (VM3_products.isEmpty()) {
                VM3_products.add(new VM3_Product("레드", 4500, 10));
                VM3_products.add(new VM3_Product("블루", 4500, 10));
                VM3_products.add(new VM3_Product("실버", 4300, 8));
            }
        }

        @Override
        protected boolean VM3_validatePurchase(VM3_IProduct VM3_p, Scanner VM3_sc) {
            System.out.print("성인입니까? (Y/N): ");
            String VM3_ans = VM3_sc.nextLine().trim().toUpperCase(Locale.ROOT);
            if (!VM3_ans.equals("Y")) {
                System.out.println("미성년자는 구매할 수 없습니다.");
                return false;
            }
            return true;
        }
    }

    // =========================
    // 실행 진입점
    // =========================
    public static void main(String[] args) {
        try (Scanner VM3_sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n[VM3 Root 자판기 선택]");
                System.out.println("1) 음료 자판기");
                System.out.println("2) 라면 자판기");
                System.out.println("3) 담배 자판기(성인 인증)");
                System.out.println("0) 종료");
                System.out.print("선택: ");
                String VM3_sel = VM3_sc.nextLine().trim();

                VM3_VendingMachine VM3_machine = null;
                switch (VM3_sel) {
                    case "1": VM3_machine = new VM3_BeverageMachine();  break;
                    case "2": VM3_machine = new VM3_RamenMachine();      break;
                    case "3": VM3_machine = new VM3_CigaretteMachine();  break;
                    case "0": System.out.println("프로그램 종료."); return;
                    default : System.out.println("잘못된 입력입니다."); continue;
                }
                VM3_machine.VM3_run(VM3_sc);
            }
        }
    }
}
