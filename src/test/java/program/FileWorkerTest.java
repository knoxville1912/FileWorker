package program;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.MapperReader;
import test.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileWorkerTest {

    @Test
    void testWrite2XLS() throws IOException, InvalidFormatException {
        FileWorker.writeFileXlS("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsersXLS",
                List.of(new User(2L, "Egor123", "egorka@mail.ru")),
                user -> new String[]{String.valueOf(user.getId()), user.getLogin(), user.getEmail()},
                new String[]{"id", "name", "login"},
                "users");

//        Assertions.assertTrue(new File("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsers.xls").exists());
//
//
//        XSSFWorkbook workbook = new XSSFWorkbook(new File("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsers.xls"));
//        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsers.xls"));
//        br.readLine();
//        Assertions.assertEquals("Egor123", br.readLine().split(";")[1]);
//        br.close();


    }

    @Test
    void testWrite2TXT() throws IOException, InvalidFormatException {
        FileWorker.writeFileTXT("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsersTXT",
                List.of(new User(3L, "Fuctor", "factor@mail.ru"),
                        new User(4L, "Slactor", "slayer@mail.com")),
                Separator.SEMICOLON,
                user -> new String[]{String.valueOf(user.getId()), user.getLogin(), user.getEmail()});
    }

    @Test
    void testWrite2CSV() throws IOException, InvalidFormatException {
        FileWorker.writeFileCSV("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\writtenUsersCSV",
                List.of(new User(1L, "Anny", "anna@mail.ru"),
                        new User(4L, "Luckyguy", "lucky@mail.com")),
                Separator.SEMICOLON,
                user -> new String[]{String.valueOf(user.getId()), user.getLogin(), user.getEmail()},
                new String[]{"id", "name", "login"});
    }

    @Test
    void testReadCSV() throws IOException {
        List<User> userList = FileWorker.readFileCSV(
                new File("C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\files\\Users.csv"),
                new MapperReader());
        Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Assertions.assertEquals("Egor123", userMap.get(1L).getLogin());
        Assertions.assertEquals("vlad@gmail.com", userMap.get(2L).getEmail());
    }

    @Test
    void testReadXLS() throws IOException {
        List<User> userList = FileWorker.readFileXLS(
                "C:\\Users\\Danko\\IdeaProjects\\FileWorker\\files\\files\\Users2.xls",
                new MapperReader(),
                true,
                User.class);
        Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Assertions.assertEquals("John", userMap.get(1L).getLogin());
        Assertions.assertEquals("mike@mail.com", userMap.get(2L).getEmail());
    }
}
