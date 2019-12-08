package bartlek.fakturomat;

import java.io.Serializable;

public class Company implements Serializable {
        public static String name = null;
        public static String adress = null;
        public static String nip  = null;
        public enum PROPERTIES {name, adress, nip};


        public Company(String name, String adress, String nip){
                this.name = name;
                this.adress = adress;
                this.nip = nip;
        }

        public static void setAdress(String adress) {
                Company.adress = adress;
        }

        public static void setName(String name) {
                Company.name = name;
        }

        public static void setNip(String nip) {
                Company.nip = nip;
        }

        public static String getAdress() {
                return adress;
        }

        public static String getName() {
                return name;
        }

        public static String getNip() {
                return nip;
        }

}
