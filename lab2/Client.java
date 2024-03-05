
public class Client {

    String name;

    private int[] timeInterval = new int[2];

    public Client(String name, ClientType type, int start, int end) {
        this.name = name;        
        this.type = type;
        this.timeInterval[0] = start;
        this.timeInterval[1] = end;
    }

    private ClientType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClientType getType() {
        return type;
    }

    public void setType(ClientType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Client " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Client)) {
            return false;
        }
        Client other = (Client) obj;
        return name.equals(other.name);

    }

    public int[] getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int start, int end) {
        this.timeInterval[0] = start;
        this.timeInterval[1] = end;
    }
}
