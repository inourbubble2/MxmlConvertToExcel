public class PairDTO {
    private String typeOfColumn;
    private String headerText;
    private String dataField;
    private String flexName;
    private String sp;
    private String programName;

    public PairDTO() {
    }

    public PairDTO(String typeOfColumn) {
        this.typeOfColumn = typeOfColumn;
    }
    public PairDTO(String typeOfColumn, String headerText, String dataField, String flexName, String programName) {
        this.typeOfColumn = typeOfColumn;
        this.headerText = headerText;
        this.dataField = dataField;
        this.flexName = flexName;
        this.programName = programName;
    }

    public PairDTO(String flexName, String sp, String programName) {
        this.flexName = flexName;
        this.sp = sp;
        this.typeOfColumn = "SP용 쿼리";
        this.programName = programName;
    }

    public String getTypeOfColumn() {
        return typeOfColumn;
    }

    public void setTypeOfColumn(String typeOfColumn) {
        this.typeOfColumn = typeOfColumn;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    public String getFlexName() {
        return flexName;
    }

    public void setFlexName(String flexName) {
        this.flexName = flexName;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
