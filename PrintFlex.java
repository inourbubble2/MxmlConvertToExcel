import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PrintFlex {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<PairDTO> pairList = new ArrayList<>();
    static String[] columnArray = {
            "<kut:KUTDataGridColumn", //grid type component
            "<kut:KUTTextInput", //text type component
            "<kut:KUTRadioGroupBox", //radio type component
            "<kut:KUTDateField", //date type component
            "DropDownList", //combo type component
            "<kut:KUTTextArea" //textarea type component
    };
    public static void main(String args[]) {
        String fileName = sc.next(); //파일 이름 입력
        String readFileText = readFile(fileName); //파일 텍스트 String으로
        String[] lineText = readFileText.split("\\r\\n"); //줄바꿈 단위로 String 끊어서 배열에 담기

        lineText = removeRemarks(lineText); // 주석 삭제하기

        pairList.add(new PairDTO(
                "타입",
                "text",
                "id",
                "textAlign",
                "enabled",
                "required",
                "visible",
                "editable")); //테이블 헤드

        for(String line : lineText) {
            for(String column : columnArray) {
                if(line.contains(column)){ //해당 라인이 컴포넌트를 가르키는 라인이면
                    PairDTO pair = new PairDTO(column);
                    if(line.contains("/>")){ //코드가 한 줄이면
                        pair = returnPair(pair, line);
                        pairList.add(pair);
                    } else { //코드가 한 줄 이상이면
                        int idx = getLineIdx(lineText, line); //라인의 위치 찾기
                        pair = returnMultipleLinePair(lineText, line, idx, pair);
                        pairList.add(pair);
                    }
                } //end line.contains(column) if
            } //end column for
        } // end lineText for

        for(PairDTO pair : pairList) { //칼럼명을 보기 쉬운 단어로 바꾸기
            switch (pair.getTypeOfColumn()) {
                case "<kut:KUTDataGridColumn" :
                    pair.setTypeOfColumn("grid");
                    break;
                case "<kut:KUTTextInput" :
                    pair.setTypeOfColumn("text");
                    break;
                case "<kut:KUTRadioGroupBox" :
                    pair.setTypeOfColumn("radio");
                    break;
                case "<kut:KUTDateField" :
                    pair.setTypeOfColumn("date");
                    break;
                case "DropDownList" :
                    pair.setTypeOfColumn("combo");
                    break;
                case "<kut:KUTTextArea" :
                    pair.setTypeOfColumn("textarea");
                    break;
            }
        }

        for(PairDTO pair : pairList) { //출력
            System.out.print(pair.getTypeOfColumn()+"-");
            System.out.print(pair.getHeaderText()+"-");
            System.out.print(pair.getDataField()+"-");
            System.out.print(pair.getTextAlign()+"-");
            System.out.print(pair.getEnabled()+"-");
            System.out.print(pair.getRequired()+"-");
            System.out.print(pair.getVisible()+"-");
            System.out.print(pair.getEditable()+"-");
            System.out.println();
        }

       getTabList(lineText); //tab 출력
    }

    private static String[] removeRemarks(String[] lineText) { //주석을 지우는 메소드
        int startIdx = 0;
        int endIdx = 0;

        for(String line : lineText) {
            if(line.contains("<!--")){ //주석 시작 기호를 발견하면
                startIdx = getLineIdx(lineText, line); //해당 라인을 startIdx 로
                endIdx = getEndIdx(startIdx, lineText, "-->"); // "-->" 를 찾아서 해당 라인을 endIdx로
                for(int i = startIdx; i<endIdx; i++) {
                    lineText[i] = ""; //startIdx ~ endIdx를 삭제한다
                }
            }
        }
        return lineText;
    }

    private static int getEndIdx(int startIdx, String[] lineText, String word){ //특정 단어가 포함된 라인의 마지막 인덱스를 구하는 메소드
        int endIdx = 0;
            for (int i = 0; i<lineText.length; i++) {
                if (lineText[startIdx + i].contains(word)) {
                    endIdx = startIdx + i;
                    break;
                }
            }
        return endIdx;
    }

    private static int getLineIdx(String[] lineText, String line) { //특정 라인의 인덱스를 구하는 메소드
        int idx = 0;
        for (int i = 0; i < lineText.length; i++) {
            if (lineText[i].equals(line)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    private static PairDTO returnMultipleLinePair(String[] lineText, String line, int idx, PairDTO pair) { //컴포넌트는 하나지만 여러 줄인 코드를 한 줄로 만드는 메소드
        for (int i = 1; i<999; i++) {
            if (lineText[idx + i].contains("/>")) {
                line += lineText[idx + i];
                pair = returnPair(pair, line);
                break;
            } else {
                line += lineText[idx + i];
            }
        }
        return pair;
    }

    private static void getTabList(String[] lineText){ //코드가 한 줄인 컴포넌트의 속성을 구하는 메소드
        for(String line : lineText){
            if(line.contains("<kut:KUTTabNavigator")){
                int startIdx = getLineIdx(lineText, line);
                int endIdx = getEndIdx(startIdx, lineText, "</kut:KUTTabNavigator>");
                for(int i = 1; i + startIdx < endIdx; i++){
                    if (lineText[startIdx + i].contains("id=") && lineText[startIdx + i].contains("label=")) {
                        System.out.println(lineText[startIdx + i]);
                    }
                }
            }
        }
    }

    static String[] gridArr = {"headerText=",  "dataField=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] textArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] radioArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] dateArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] comboArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] textareaArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};

    static PairDTO returnPair(PairDTO pair, String line) { //한 줄인 컴포넌트의 속성을 구하는 메소드

        String[] arr = new String[7];
        switch (pair.getTypeOfColumn()){
            case "<kut:KUTDataGridColumn"  :
                arr = gridArr;
                break;
            case "<kut:KUTTextInput" :
                arr = textArr;
                break;
            case "<kut:KUTRadioGroupBox" :
                arr = radioArr;
                break;
            case "<kut:KUTDateField" :
                arr = dateArr;
                break;
            case "DropDownList" :
                arr = comboArr;
                break;
            case "<kut:KUTTextArea" :
                arr = textareaArr;
                break;
        }

        for(String text : arr) {
            if(line.contains(text)) {
                String inputText = line.substring(line.indexOf(text)+text.length()+1, line.indexOf("\"",line.indexOf(text)+text.length()+1));
                if(inputText.equals("")||inputText.isEmpty())
                    inputText = "null";
                switch (text) {
                    case "headerText=" :
                    pair.setHeaderText(inputText);
                    break;
                    case "labelText=" :
                        pair.setHeaderText(inputText);
                        break;
                    case "dataField=" :
                        pair.setDataField(inputText);
                        break;
                    case "id=" :
                        pair.setDataField(inputText);
                        break;
                    case "textAlign=" :
                        pair.setTextAlign(inputText);
                        break;
                    case "enabled=" :
                        pair.setEnabled(inputText);
                        break;
                    case "required=" :
                        pair.setRequired(inputText);
                        break;
                    case "visible=" :
                        pair.setVisible(inputText);
                        break;
                    case "editable=" :
                        pair.setEditable(inputText);
                        break;
                }
            }
        }
        return pair;
    }

    public static String readFile(String inputText) { //파일을 읽는 메소드
        String returnString = "";


        //프로그램명으로 읽는 방법

        if(inputText.contains("(")) {
            inputText = inputText.substring(inputText.indexOf("(")+1,inputText.lastIndexOf(")"));
        }

        String[] splitText = inputText.split("_");

        String folderName = splitText[0];
        String parentFolderName = splitText[1];

        inputText = folderName + "\\\\" + parentFolderName + "\\\\" + inputText;
        //파일 url을 읽는 방법
        //inputText = inputText.substring(0,inputText.lastIndexOf("."));
        //inputText.replace("/","\\");

        inputText = "C:\\\\kutproject\\\\workspace\\\\KUT_Main_Application\\\\flex_src\\\\"+inputText+".mxml";
        //파일경로

        try{
            File file = new File(inputText);//파일 객체 생성
            FileReader filereader = new FileReader(file); //입력 스트림 생성
            BufferedReader bufReader = new BufferedReader(filereader); //입력 버퍼 생성
            String line = "";
            while((line = bufReader.readLine()) != null){
                returnString += line + "\r\n";
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.
            bufReader.close();
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }

        //글 읽는지 테스트
        //System.out.println(returnString);
        return returnString;
    }
}