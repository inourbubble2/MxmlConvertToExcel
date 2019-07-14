public class PairDTO {
    private String typeOfColumn;
    /*
     * <kut:KUTDataGridColumn /> - grid (headerText, dataField, textAlign, visible, editable, enabled)
     * <kut:KUTTextInput /> - text (id, labelText, visible, enabled, textAlign
     * <kut:KUTRadioGroupBox /> - radio (id)
     * <kut:KUTDateField /> - date (id, labelText)
     * <code:CprSeCdDropDownList /> - combo (id, labelText, visible)
     * <<kut:KUTTextArea /> - textarea (id, labelText)
     * */
    private String headerText;
    private String dataField;
    private String textAlign;
    private String enabled;
    private String required;
    private String visible;
    private String editable;

    public PairDTO(String typeOfColumn) {
        this.typeOfColumn = typeOfColumn;
    }

    public PairDTO(String typeOfColumn, String headerText, String dataField, String textAlign, String enabled, String required, String visible, String editable) {
        this.typeOfColumn = typeOfColumn;
        this.headerText = headerText;
        this.dataField = dataField;
        this.textAlign = textAlign;
        this.enabled = enabled;
        this.required = required;
        this.visible = visible;
        this.editable = editable;
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

    public String getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }
}
