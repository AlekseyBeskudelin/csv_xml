import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Employee> listOfEmploee;

    public static void main(String[] args) throws ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameXml = "data.xml";
        List<Employee> listOfEmploee = parseCSV(columnMapping, fileName);
        String json = listToJson(listOfEmploee);
        writeString(json, "new_data.json");
        List<Employee> listXML = parseXML(fileNameXml);
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, "new_data_2.json");


    }

    public static List<Employee> parseCSV(String[] columnMapp, String file) {

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapp);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String listToJson(List<Employee> listOfEmploee) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(listOfEmploee, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> list = new ArrayList<>();
        Employee employee = new Employee();
        long id = 0;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;
        File file = new File(fileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            doc = dbf.newDocumentBuilder().parse(file);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        Node rootNode = doc.getFirstChild();
        System.out.println("Root " + rootNode.getNodeName());
        NodeList rootChilds = rootNode.getChildNodes();
        for (int i = 0; i < rootChilds.getLength(); i++) {
            if (rootChilds.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            NodeList emploeeList = rootChilds.item(i).getChildNodes();
            for (int a = 0; a < emploeeList.getLength(); a++) {
                if (emploeeList.item(a).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                //System.out.println("Вложенные " + emploeeList.item(a).getTextContent());
                switch (emploeeList.item(a).getNodeName()) {
                    case "id":
                        id = Long.valueOf(emploeeList.item(a).getTextContent());
                        break;
                    case "firstName":
                        firstName = emploeeList.item(a).getTextContent();
                        break;
                    case "lastName":
                        lastName = emploeeList.item(a).getTextContent();
                        break;
                    case "country":
                        country = emploeeList.item(a).getTextContent();
                        break;
                    case "age":
                        age = Integer.valueOf(emploeeList.item(a).getTextContent());
                        break;
                }
            }
            employee = new Employee(id, firstName, lastName, country, age);
            list.add(employee);
        }

        return list;

    }

}



