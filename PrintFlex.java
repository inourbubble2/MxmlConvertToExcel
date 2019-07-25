import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class PrintFlex {

    static String[] columnArray = {
            "<kut:KUTDataGridColumn", //grid
            "<kut:KUTTextInput", //text
            "<kut:KUTRadioGroupBox", //radio
            "<kut:KUTDateField", //date
            "DropDownList", //combo
            "<kut:KUTTextArea", //textarea

            //없어서 추가하는 것들
            "KUTNumericStepper"
    };
    public static void main(String args[]) {
        ArrayList<String> fileNameList = getFileNameList();
        ArrayList<PairDTO> pairList= new ArrayList<>();
        for(int i=0; i<fileNameList.size(); i++) {
            String fileName = fileNameList.get(i);
            String readFileText = readFile(fileName); //파일 텍스트 String으로


            if(readFileText==null) {
                continue;
            }
            String[] lineText = readFileText.split("\\r\\n"); //줄바꿈 단위로 String 끊어서 배열에 담기

            String programName = getProgramName(lineText); //프로그램이름 입력



            lineText = removeRemarks(lineText); // 주석 삭제하기

            if(i==0) {
                pairList.add(new PairDTO("타입","text","id","flex파일명", "프로그램명")); //테이블 헤드
            }

            //sp쿼리 출력
            ArrayList<String> spList = getSpList(lineText);

            for(String sp : spList) {
                PairDTO pairDTO = new PairDTO(fileName, sp, programName);
                pairList.add(pairDTO);
            }

            for(String line : lineText) {
                for(String column : columnArray) {
                    if(line.contains(column)){ //해당 라인이 컴포넌트를 가르키는 라인이면
                        PairDTO pair = new PairDTO(column);
                        pair.setProgramName(programName);
                        pair.setFlexName(fileName);
                        if(line.contains("/>")){ //코드가 한 줄이면
                            pair = returnPair(pair, line);

                        } else { //코드가 한 줄 이상이면
                            int idx = getLineIdx(lineText, line); //라인의 위치 찾기
                            pair = returnMultipleLinePair(lineText, line, idx, pair);
                        }
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
                            case "KUTNumericStepper" :
                                pair.setTypeOfColumn("year");
                                break;
                        }
                        pairList.add(pair);
                    } //end line.contains(column) if
                } //end column for
            } // end lineText for



            

            System.out.println(fileName+": "+"job clear!!");
            outputExcel(pairList);
            //getTabList(lineText); //tab 출력
        }
        //System.out.println("job clear!!");
        
    }

    private static String getProgramName(String[] lineText) {
        String programName = "";
        for(String line : lineText) {
            if(line.contains("[프로그램명]")) {
                if(line.contains(":")) {
                    programName = line.substring(line.indexOf(":")+1);
                } else {
                    programName = line.substring(line.indexOf("]")+1);
                }
                break;
            }
        }
        return programName;
    }

    private static ArrayList<String> getSpList(String[] lineText) {
        ArrayList<String> select = new ArrayList<>();
        for(String line : lineText) {
            if(line.contains(".addMultiSelect")) {
                line = line.substring(line.indexOf("(")+1, line.indexOf(")"));
                select.add(line);
            }
        }
        return select;
    }

    static String[] removeRemarks(String[] lineText) { //주석을 지우는 메소드
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

    static int getEndIdx(int startIdx, String[] lineText, String word){ //주석 삭제 시 마지막 인덱스를 구하는 메소드
        int endIdx = 0;
        for (int i = startIdx; i<lineText.length; i++) {

            if (lineText[i].contains(word)) {
                endIdx = i;
                break;
            }
        }
        return endIdx;
    }

    static int getLineIdx(String[] lineText, String line) { //해당 라인의 인덱스를 구하는 메소드
        int idx = 0;
        for (int i = 0; i < lineText.length; i++) {
            if (lineText[i].equals(line)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    static PairDTO returnMultipleLinePair(String[] lineText, String line, int idx, PairDTO pair) { //여러 줄인 컴포넌트의 속성을 구하는 메소드
        for (int i = 1; i<999; i++) {

            if(idx+i>=lineText.length) {
                break;
            }

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

    static void getTabList(String[] lineText){

        for(String line : lineText){
            if(line.contains("<kut:KUTTabNavigator")){
                int startIdx = getLineIdx(lineText, line);
                int endIdx = getEndIdx(startIdx, lineText, "</kut:KUTTabNavigator");
                for(int i = 1; i + startIdx < endIdx; i++){
                    if (lineText[startIdx + i].contains("id=") && lineText[startIdx + i].contains("label=")) {
                        String tapLine = lineText[startIdx + i];

                        //탭목록 출력

                        String label = tapLine.substring(tapLine.indexOf("label=\"")+7, tapLine.indexOf("\"",tapLine.indexOf("label=\"")+7));
                        String id = tapLine.substring(tapLine.indexOf("id=\"")+4, tapLine.indexOf("\"",tapLine.indexOf("id=\"")+4));

                        System.out.println(label+"-"+id);
                        // System.out.println(tapLine);
                    }
                }
            }
        }
    }

    /*
     * <kut:KUTDataGridColumn /> - grid (headerText, dataField, textAlign, visible, editable, enabled)
     * <kut:KUTTextInput /> - text (id, labelText, visible, enabled, textAlign
     * <kut:KUTRadioGroupBox /> - radio (id)
     * <kut:KUTDateField /> - date (id, labelText)
     * <code:CprSeCdDropDownList /> - combo (id, labelText, visible)
     * <<kut:KUTTextArea /> - textarea (id, labelText)
     * */
    static String[] gridArr = {"headerText=",  "dataField=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    static String[] textArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    //static String[] radioArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    //static String[] dateArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    //static String[] comboArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};
    //static String[] textareaArr = {"labelText=",  "id=", "textAlign=", "enabled=", "required=", "visible=", "editable="};

    static PairDTO returnPair(PairDTO pair, String line) { //한 줄인 컴포넌트의 속성을 구하는 메소드

        String[] arr = new String[7];
        switch (pair.getTypeOfColumn()){
            case "<kut:KUTDataGridColumn"  :
                arr = gridArr;
                break;
            default :
                arr = textArr;
                break;
            /*case "<kut:KUTTextInput" :
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
                break;*/
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
                }
            }
        }
        return pair;
    }

    static String readFile(String inputText) { //파일을 읽는 메소드
        String returnString = "";


        //프로그램명으로 읽는 방법

        if(inputText.contains("(")) {
            inputText = inputText.substring(inputText.indexOf("(")+1,inputText.lastIndexOf(")"));
        }

        if(!inputText.contains("_")) {
            return null;
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
            //파일 객체 생성
            File file = new File(inputText);
            //입력 스트림 생성
            FileReader filereader = new FileReader(file);
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
                line = line.replace(" ", "");
                line = line.replace("\t", "");
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

    static void outputExcel(ArrayList<PairDTO> pairList) {

        // Workbook 생성
        Workbook xlsWb = new HSSFWorkbook(); // Excel 2007 이전 버전
        Workbook xlsxWb = new XSSFWorkbook(); // Excel 2007 이상

        // *** Sheet-------------------------------------------------
        // Sheet 생성
        Sheet sheet1 = xlsWb.createSheet("firstSheet");

        // 컬럼 너비 설정
        //sheet1.setColumnWidth(0, 10000);
        //sheet1.setColumnWidth(9, 10000);
        // ----------------------------------------------------------

        // *** Style--------------------------------------------------
        // Cell 스타일 생성
        //CellStyle cellStyle = xlsWb.createCellStyle();

        // 줄 바꿈
        //cellStyle.setWrapText(true);


        // Cell 색깔, 무늬 채우기
        //cellStyle.setFillForegroundColor(HSSFColor.LIME.index);
        //cellStyle.setFillPattern(CellStyle.BIG_SPOTS);

        Row row = null;
        Cell cell = null;
        //----------------------------------------------------------

        // 첫 번째 줄

        for(int i=0; i<pairList.size(); i++) {
            row = sheet1.createRow(i);
            PairDTO pair = pairList.get(i);
            if(pair.getSp()==null) {
                cell = row.createCell(0);
                cell.setCellValue(pair.getFlexName());
                cell = row.createCell(1);
                cell.setCellValue(pair.getDataField());
                cell = row.createCell(2);
                cell.setCellValue(pair.getHeaderText());
                cell = row.createCell(3);
                cell.setCellValue(pair.getTypeOfColumn());
                cell = row.createCell(4);
                cell.setCellValue(pair.getProgramName());
            } else {
                cell = row.createCell(0);
                cell.setCellValue(pair.getFlexName());
                cell = row.createCell(1);
                cell.setCellValue(pair.getSp());
                cell = row.createCell(3);
                cell.setCellValue(pair.getTypeOfColumn());
                cell = row.createCell(4);
                cell.setCellValue(pair.getProgramName());
            }
        }

        // excel 파일 저장
        try {
            File xlsFile = new File("C:/Temp/testExcel.xls");
            FileOutputStream fileOut = new FileOutputStream(xlsFile);
            xlsWb.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static ArrayList<String> getFileNameList() {
        ArrayList<String> fileNameList = new ArrayList<>();
        String path = "C:\\kutproject\\workspace\\KUT_Main_Application\\flex_src";
        File flexFolder = new File(path);

        File[] upperFolderList = flexFolder.listFiles();

        for(File upperFolder : upperFolderList) {
            File[] folderList = upperFolder.listFiles();
            if(upperFolder.isDirectory()) {
                for(File folder : folderList) {
                    if(folder.isDirectory()) {
                        File[] fileList = folder.listFiles();
                        for(File file : fileList) {
                            if(file.isFile()) {
                                String fileName = file.getName();
                                if(fileName.substring(fileName.lastIndexOf(".")).equals(".mxml"));
                                fileNameList.add(fileName.substring(0,fileName.lastIndexOf(".")));
                            }
                        }
                    }
                }
            }
        }
        /*
        //파일 읽어들이는지 테스트
        for(String outputText : fileNameList) {
            System.out.println(outputText);
        }
        */
        return fileNameList;
    }
}
