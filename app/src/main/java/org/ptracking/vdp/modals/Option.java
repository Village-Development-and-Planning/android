package org.ptracking.vdp.modals;

import org.ptracking.vdp.other.Constants;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Option extends BaseObject {
    private String type;
    private Text text;
    private String position;
    private String imageData;
    private String value;

    public Option() {
        this.value = "";
    }

    public Option(Option other) {
        this();
        this.type = other.type;
        this.text = other.text;
        this.position = other.position;
        this.imageData = other.imageData;
        this.value = other.value;
    }

    public Option(String type, Text text, String position) {
        this();
        this.type = type;
        this.text = text;
        this.position = position;
    }

    public Option(String type, Text text, String position, String imageData) {
        this();
        this.type = type;
        this.text = text;
        this.position = position;
        this.imageData = imageData;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTextString() {
        switch (Constants.APP_LANGUAGE) {
            case TAMIL:
                return text.getTamil();
            default:
                return text.getEnglish();
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImageData() {
        return imageData;
    }

    public String getType() {
        return type;
    }

    public Text getText() {
        return text;
    }

    public String getPosition() {
        return position;
    }

//    private void writeObject(ObjectOutputStream os) throws IOException {
//        os.writeUTF(type);
//        os.writeObject(text);
//        os.writeUTF(position);
//        os.writeUTF(imageData);
//        os.writeUTF(value);
//    }
//
//    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
//        type = is.readUTF();
//        text = (Text) is.readObject();
//        position = is.readUTF();
//        imageData = is.readUTF();
//        value = is.readUTF();
//    }
}
