package org.ptracking.vdp.modals;

public class Text extends BaseObject {
    private String english;
    private String tamil;

    public Text() {
    }

    public Text(String english, String tamil) {
        this.english = english;
        this.tamil = tamil;
    }

    public String getEnglish() {
        return english;
    }

    public String getTamil() {
        return tamil;
    }

//    private void writeObject(ObjectOutputStream os) throws IOException {
//        os.writeUTF(english);
//        os.writeUTF(tamil);
//    }
//
//    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
//        english = is.readUTF();
//        tamil = is.readUTF();
//    }
}