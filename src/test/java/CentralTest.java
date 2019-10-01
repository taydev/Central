import org.mindrot.bcrypt.BCrypt;

public class CentralTest {

    public static void main(String[] args) {
        System.out.println(BCrypt.gensalt(8));
    }
}
