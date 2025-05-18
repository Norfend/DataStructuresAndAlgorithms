package task3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.lang.Math.toIntExact;

public class Hash {

    private static final Reporter[] reporters = {
            new Reporter(11, "Mirek"),
            new Reporter(11, "Jarka"),
            new Reporter(11, "Jindra"),
            new Reporter(11, "Rychlonozka"),
            new Reporter(11, "Cervenacek")
    };

    private static int currentWriter = -1;

    public static void main(String[] args) {
        boolean acceptNews = false;
        boolean acceptedSettings = false;
        boolean deleteNews = false;
        boolean printing = false;
        String[] command;

        try (BufferedInputStream input = new BufferedInputStream(System.in)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            while (reader.ready()) {
                String nextLine = reader.readLine();
                if (!nextLine.isEmpty()) {
                    command = lineParser(nextLine);
                } else {
                    System.err.println("Error: Chybny vstup!");
                    continue;
                }
                switch (command[0]) {
                    case "Input news": {
                        acceptNews = true;
                        deleteNews = false;
                        printing = false;
                        currentWriter = -1;
                        break;
                    }
                    case "Table settings": {
                        String[] settings = command[1].split(" ");
                        if (settings.length <= reporters.length && !acceptedSettings) {
                            for (int i = 0; i < settings.length; i++) {
                                try {
                                    reporters[i].setInitialTableSize(Integer.parseInt(settings[i]));
                                    reporters[i].setTableSize(Integer.parseInt(settings[i]));
                                    reporters[i].setNews(new Node[Integer.parseInt(settings[i])]);
                                } catch (Exception e) {
                                    System.err.println(e.getMessage());
                                    break;
                                }
                            }
                        } else {
                            System.err.println("Error: Chybny vstup!");
                        }
                        break;
                    }
                    case "Writer settings": {
                        currentWriter = Integer.parseInt(command[1]);
                        acceptNews = false;
                        deleteNews = false;
                        printing = true;
                        break;
                    }
                    case "Print": {
                        if (currentWriter != -1) {
                            dataPrinter(reporters[currentWriter - 1]);
                            acceptNews = false;
                            deleteNews = false;
                        } else {
                            System.err.println("Error: Chybny vstup!");
                        }
                        break;
                    }
                    case "Delete": {
                        if (currentWriter != -1) {
                            acceptNews = false;
                            deleteNews = true;
                        } else {
                            System.err.println("Error: Chybny vstup!");
                        }
                        break;
                    }
                    case "Error": {
                        System.err.println("Error: Chybny vstup!");
                        acceptNews = false;
                        deleteNews = false;
                        currentWriter = -1;
                        break;
                    }
                    case "News": {
                        if (acceptNews) {
                            for (Reporter reporter : reporters) {
                                reporter.addNews(command[1], 1);
                            }
                        } else if (deleteNews) {
                            reporters[currentWriter - 1].deleteNews(command[1]);
                        } else if (printing) {
                            newsPrinter(command[1], reporters[currentWriter - 1]);
                        }
                        break;
                    }
                    default: {
                        System.err.println("Line parser error" + command[0]);
                        System.exit(1);
                    }
                }
                if (!acceptedSettings) acceptedSettings = true;
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String[] lineParser(String inputLine) {
        char[] line = inputLine.toCharArray();
        String[] result = new String[2];
        if (line[0] == '#') {
            if (line.length < 2) {
                result[0] = "Error";
            }
            else if (line[1] == 'a') {
                result[0] = "Input news";
            }
            else if (line[1] == 'i') {
                result[0] = "Table settings";
                result[1] = spaceCutter(inputLine.substring(2));
            }
            else if (49 <= line[1] && line[1] <= 53) {
                result[0] = "Writer settings";
                result[1] = String.valueOf(line[1]);
            }
            else if (line[1] == 'p') {
                result[0] = "Print";
            }
            else if (line[1] == 'd') {
                result[0] = "Delete";
            }
            else {
                result[0] = "Error";
            }
        }
        else {
            result[0] = "News";
            result[1] = spaceCutter(inputLine);
        }
        return result;
    }

    private static String spaceCutter(String inputString) {
        char[] stringWithoutSpaces = inputString.toCharArray();
        int startOfNews = 0;
        int endOfNews = stringWithoutSpaces.length;
        for (int i = 0; i < stringWithoutSpaces.length; i++) {
            if (stringWithoutSpaces[i] != ' ') {
                startOfNews = i;
                break;
            }
        }
        for (int i = stringWithoutSpaces.length - 1; i > 0; i--) {
            if (stringWithoutSpaces[i] != ' ') {
                endOfNews = i + 1;
                break;
            }
        }
        return inputString.substring(startOfNews, endOfNews);
    }

    private static void dataPrinter(Reporter reporter) {
        String result = reporter.getReporterName() + "\n\t" + reporter.getTableSize() + " " + reporter.getMessageCount();
        System.out.println(result);
    }

    private static void newsPrinter(String inputString, Reporter reporter) {
        Node node = reporter.findMessageByValue(inputString);
        if (node != null) {
            System.out.println(node);
        } else {
            System.out.println("\t" + inputString + " " + -1 + " " + 0);
        }
    }

    private static class Reporter {

        private int messageCount = 0;

        private int initialTableSize;

        private int tableSize;

        private final String reporterName;

        private Node[] news;

        public Reporter(int initialSize, String name) {
            this.initialTableSize = initialSize;
            this.tableSize = initialSize;
            this.reporterName = name;
            this.news = new Node[this.tableSize];
        }

        public void addNews(String message, int newsCount) {
            Node news = findMessageByValue(message);
            int messageIndex;
            if (news != null && news.getNodeId() > -1) {
                news.setNodeCount(news.getNodeCount() + 1);
                return;
            }
            int loadFactor = (int) (getTableSize() * 0.7);
            messageIndex = toIntExact(hashCalculation(message));
            if (! insertMessage(messageIndex, message, newsCount)) {
                int newMessageIndex = linearProbing(messageIndex, message, true);
                insertMessage(newMessageIndex, message, newsCount);
            }
            if (getMessageCount() > loadFactor) {
                setTableSize(getTableSize() * 2);
                rehash();
            }
        }

        public void deleteNews(String message) {
            int messageIndex = toIntExact(hashCalculation(message));
            if (! deleteMessage(messageIndex, message)) {
                int newMessageIndex = linearProbing(messageIndex, message, false);
                if (newMessageIndex != -1) {
                    deleteMessage(newMessageIndex, message);
                }
            }
            if (getMessageCount() < getTableSize() * 0.3 && getTableSize() != getInitialTableSize()) {
                int newSize = Math.max(getTableSize() / 2, getInitialTableSize());
                setTableSize(newSize);
                rehash();
            }
        }

        public Node findMessageByValue(String message) {
            for (Node node : getNews()) {
                if (node != null && node.getNodeValue().equals(message)) {
                    return node;
                }
            }
            return null;
        }

        private boolean insertMessage(int index, String message, int newsCount) {
            Node[] nodes = getNews();
            if (nodes[index] == null) {
                nodes[index] = new Node(index, newsCount, message);
                setMessageCount(getMessageCount() + 1);
                return true;
            } else if (nodes[index].getNodeValue().equals(message) && nodes[index].getNodeId() > -1) {
                nodes[index].setNodeCount(nodes[index].getNodeCount() + 1);
                return true;
            } else if (nodes[index].getNodeValue().equals(message) && nodes[index].getNodeId() == -1) {
                nodes[index].setNodeId(index);
                nodes[index].setNodeCount(nodes[index].getNodeCount() + 1);
                setMessageCount(getMessageCount() + 1);
                return true;
            } else if (nodes[index].getNodeId() == -1) {
                nodes[index].setNodeId(index);
                nodes[index].setNodeValue(message);
                nodes[index].setNodeCount(1);
                setMessageCount(getMessageCount() + 1);
                return true;
            }
            return false;
        }

        private boolean deleteMessage(int index, String message) {
            Node[] nodes = getNews();
            if (nodes[index] == null) {
                return false;
            }
            if (nodes[index].getNodeValue().equals(message) && nodes[index].getNodeCount() > 1) {
                nodes[index].setNodeCount(nodes[index].getNodeCount() - 1);
                return true;
            } else if (nodes[index].getNodeValue().equals(message) && nodes[index].getNodeCount() == 1) {
                nodes[index].setNodeId(-1);
                nodes[index].setNodeCount(0);
                setMessageCount(getMessageCount() - 1);
                return true;
            }
            else return nodes[index].getNodeValue().equals(message) && nodes[index].getNodeId() == -1;
        }

        private int linearProbing(int index, String message, boolean forInsert) {
            Node[] nodes = getNews();
            int originalIndex = index;
            index = (index + 1) % getTableSize();
            int tombstone;

            while (nodes[index] != null) {
                if (nodes[index].getNodeValue().equals(message)) {
                    return index;
                }
                if (forInsert && nodes[index].getNodeId() == -1) {
                    tombstone = index;
                    return tombstone;
                }
                index = (index + 1) % getTableSize();
                if (index == originalIndex) {
                    break;
                }
            }
            return forInsert ? index : -1;
        }

        private long hashCalculation(String news) {
            char[] newsAsCharArray = news.toCharArray();
            long hash = 0;
            long power = 1L;
            for (char c : newsAsCharArray) {
                int letter = getCharValue(c);
                long temp = letter * power;
                hash += temp;
                power = (power * 32) % getTableSize();
            }
            return hash % getTableSize();
        }

        private void rehash() {
            Node[] oldNews = getNews();
            this.news = new Node[getTableSize()];
            setMessageCount(0);
            for (Node message: oldNews) {
                if (message != null && message.getNodeId() > -1) {
                    addNews(message.getNodeValue(), message.nodeCount);
                }
            }
        }

        private int getCharValue(char c) {
            return (c == ' ') ? 31 : (c - 'a' + 1);
        }

        public String getReporterName() {
            return this.reporterName;
        }

        public int getTableSize() {
            return this.tableSize;
        }

        public void setTableSize(int tableSize) {
            this.tableSize = tableSize;
        }

        public int getInitialTableSize() {
            return this.initialTableSize;
        }

        public void setInitialTableSize(int initialTableSize) {
            this.initialTableSize = initialTableSize;
        }

        public int getMessageCount() {
            return this.messageCount;
        }

        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }

        public Node[] getNews() {
            return this.news;
        }

        public void setNews(Node[] news) {
            this.news = news;
        }
    }

    private static class Node {

        private int nodeId;

        private int nodeCount;

        private String nodeValue;

        public Node(int id, int count, String value) {
            this.nodeId = id;
            this.nodeCount = count;
            this.nodeValue = value;
        }

        public int getNodeId() {
            return nodeId;
        }

        public void setNodeId(int nodeId) {
            this.nodeId = nodeId;
        }

        public int getNodeCount() {
            return this.nodeCount;
        }

        public void setNodeCount(int nodeCount) {
            this.nodeCount = nodeCount;
        }

        public String getNodeValue() {
            return this.nodeValue;
        }

        public void setNodeValue(String nodeValue) {
            this.nodeValue = nodeValue;
        }

        @Override
        public String toString() {
            return "\t" + getNodeValue() + " " + getNodeId() + " " + getNodeCount();
        }
    }
}