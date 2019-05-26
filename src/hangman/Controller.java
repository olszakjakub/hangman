package hangman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    private Label question, error, category, nickError, end, usedLetterError, revealQuestion, allScores, wrongNickError;
    @FXML
    private Button sendButton, reset;
    @FXML
    private TextField letter, nick, usedLetters;
    @FXML
    private ImageView image;
    @FXML
    private TextArea topScorers, lastGames, playCounter;

    private String cat;
    private String q;
    int fails = 0;
    int score = 0;
    String questionX = "";

    /**
     * Funkcja przypisująca losową kategorię do zmiennej cat
     * i losowe pytanie do zmiennej q
     */
    private void getRandomQuestion(){

        Random random = new Random();
        int categoryNumber = random.nextInt(5);
        int questionNumber = random.nextInt(10);

        String[] questions = new String[10];

        switch(categoryNumber){
            case 0 : {
                try {
                    File file = new File("jedzenie.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        i++;
                    }
                    cat = "jedzenie";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1 : {
                try {
                    File file = new File("kraje.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        i++;
                    }
                    cat = "kraje";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2 : {
                try {
                    File file = new File("technologie.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        i++;
                    }
                    cat = "technologie";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 3 : {
                try {
                    File file = new File("warzywa.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        i++;
                    }
                    cat = "warzywa";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 4 : {
                try {
                    File file = new File("zespoły muzyczne.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        i++;
                    }
                    cat = "zespoły muzyczne";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                System.err.println("Error while getting random category");
            }
        }

        q = questions[questionNumber];

    }


    /**
     * Funkcja inicjalizująca grę
     */
    @FXML
    public void initialize() {
        countScoredNames();
        printTopScorers();
        printLatestGames();
        q="";
        questionX="";
        fails = 0;
        nick.setDisable(false);
        nick.setText("");
        letter.setDisable(false);
        sendButton.setDisable(false);
        usedLetters.setText("");
        reset.setVisible(false);
        end.setVisible(false);
        image.setImage(null);
        score = 0;
        revealQuestion.setVisible(false);
        getRandomQuestion();
        category.setText(cat);

        for (int i = 0; i < q.length(); i++) {
            if(q.charAt(i) == ' ') questionX +=" ";
            else questionX += "-";
        }

        question.setText(questionX);
    }

    /**
     * Funkcja obsługjąca naciśnięcie przycisku
     */
    @FXML
    public void buttonAction(){

        error.setVisible(false);
        wrongNickError.setVisible(false);
        nickError.setVisible(false);
        usedLetterError.setVisible(false);

        String let = letter.getText().toLowerCase();
        char l = let.charAt(0);

        if (letter.getText().length()!=1 ) {                                        //wypisanie błędu w przypadku nieprawidłowego
            error.setVisible(true);
        } else if (usedLetters.getText().contains(let)) {                           //wypisanie błędu w przypadku próby użycia tej samej litery
            usedLetterError.setVisible(true);
        } else if (nick.getText().contains(" ") || nick.getText().contains(",")) {  //wypisanie błędu w przypadku próby użycia nieprawidłowego nicku
            wrongNickError.setVisible(true);
        } else {
            if (nick.getText().length() > 0){                                       //sprawdzenie czy został podany nick
                nick.setDisable(true);
                boolean check = false;
                error.setVisible(false);
                nickError.setVisible(false);
                usedLetterError.setVisible(false);
                for (int i = 0; i < q.length(); i++) {                              //sprawdzenie czy podana litera występuje haśle i jeśli tak to odsłonięcie tych liter
                    if (q.toLowerCase().charAt(i) == l) {
                        questionX = questionX.substring(0, i)+q.charAt(i)+questionX.substring(i+1);
                        check = true;
                        score += 5;
                    }
                }

                if(!check){                                                         //jeśli podana litera była niepoprawna
                    fails++;                                                        //to zmieniamy obrazek oraz wynik
                    String filePath = fails + ".png";
                    File file = new File(filePath);
                    Image img = new Image(file.toURI().toString());
                    image.setImage(img);
                    score -= 2;
                }

                question.setText(questionX);
                usedLetters.setText(usedLetters.getText()+let.charAt(0)+",");

                if (questionX.toLowerCase().equals(q.toLowerCase())) {              //jeśli użytkownik wprowadził wszystkie litery
                    end.setVisible(true);
                    score += 10;
                    end.setText("Wygrałeś/aś, twój wynik to: " + score);
                    sendButton.setDisable(true);
                    nick.setDisable(true);
                    letter.setDisable(true);
                    reset.setVisible(true);
                    saveNicknameToFile(nick.getText(), score);
                }

                if (fails > 6){                                                     //jeśli użytkownikowi skończyły się szanse
                    end.setVisible(true);
                    end.setText("Przegrałeś/aś, twój wynik to: " + score);
                    sendButton.setDisable(true);
                    nick.setDisable(true);
                    letter.setDisable(true);
                    reset.setVisible(true);
                    saveNicknameToFile(nick.getText(), score);
                    revealQuestion.setVisible(true);
                    revealQuestion.setText(q);
                }

            } else {
                nickError.setVisible(true);
            }

        }
        letter.setText("");
        countScoredNames();
        printTopScorers();
        printLatestGames();
    }

    // zapisywanie nicku użytkownika do pliku
    private void saveNicknameToFile(String nick, int score){
        String fileName = "wyniki.txt";
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(nick + "," + Integer.toString(score) + '\n');
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // czytanie wszystkich linii z pliku
    private ArrayList<String> readWholeFile(String filePath){
        Scanner scanner = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            scanner = new Scanner(new File(filePath));
            while(scanner.hasNext()){
                list.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    // metoda zamienia listę graczy na set - pobiera tylko unikatowe elementy
    private Set<String> getUniqueNames(ArrayList<String> list){
        ArrayList<String> temp = extractNamesFromFile(list);
        Set<String> set = new HashSet<String>(temp);
        return set;
    }

    // metoda oddzielająca i zwracająca same imiona z pliku bez punktów po przecinku
    private ArrayList<String> extractNamesFromFile(ArrayList<String> list){
        ArrayList<String> temp = new ArrayList<>();
        String[] parts;
        for(String value : list){
            parts = value.split(",");
            temp.add(parts[0]);
        }
        return temp;
    }

    // metoda sortująca mapę po wartościach (odwrotnie)
    private Map<String, Integer> mapSort(Map<String, Integer> map){
        return map.entrySet()
                .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    // metoda zlicza liczbę gier dla poszczególnych graczy, oraz wyświetla te informacje a polu tekstowym
    private void countScoredNames(){
        playCounter.clear();
        ArrayList<String> list = readWholeFile("wyniki.txt");
        Map<String, Integer> map = new LinkedHashMap<>();
        // liczba gier w ogóle
        int sumOfGames = 0;
        Set<String> users = getUniqueNames(list);
        // w pętli dodawane są imiona i częstość wystąpienia do mapy, która następnie jest sortowana
        for(String user : users){
            map.put(user,Collections.frequency(extractNamesFromFile(list), user));
        }
        map = mapSort(map);
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            playCounter.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            sumOfGames += entry.getValue();
        }

        allScores.setText(Integer.toString(sumOfGames));
    }

    // metoda wyświetlająca ostatnich graczy i ich punktację
    private void printLatestGames(){
        lastGames.clear();
        ArrayList<String> list = readWholeFile("wyniki.txt");
        // odwrócenie listy graczy - najnowsze wpisy są na samym końcu pliku
        Collections.reverse(list);
        String[] element;
        for(String user : list){
            element = user.split(",");
            lastGames.appendText(element[0] + ": " + element[1] + "pkt. \n");
        }
    }

    // metoda wyświetlająca wyniki w kolejności od najleszego
    private void printTopScorers(){
        topScorers.clear();
        ArrayList<String> list = readWholeFile("wyniki.txt");
        // lista par do której zapisywane będą wyniki w postaci wynik, imię
        ArrayList<Pair<Integer, String>> listOfPairs = new ArrayList<>();
        Pair<Integer, String> pair;
        int i = 1;
        String[] element;
        // wszystkie pozycje z pobranej z pliku listy kolejno zapisywane są do par po rozdzieleniu, a następnie pary dodawane są do listy
        for(String user : list){
            element = user.split(",");
            pair = new Pair<>(Integer.parseInt(element[1]), element[0]);
            listOfPairs.add(pair);
        }
        // pary są sortowane wg. wyniku a następnie cała lista jest odwracana (od najwyższego wyniku)
        listOfPairs.sort(Comparator.comparing(Pair::getKey));
        Collections.reverse(listOfPairs);
        for (Pair<Integer, String> p :  listOfPairs) {
            topScorers.appendText(i + ". " + p.getValue() + ": " + p.getKey() + "pkt. \n");
            i++;
        }
    }

}