
public class Client {

    String name;

    private int visitingInterval;

    private ClientType type;

    public Client(String name, int visitingInterval, ClientType type) {
        this.name = name;
        this.visitingInterval = visitingInterval;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisitingInterval() {
        return visitingInterval;
    }

    public void setVisitingInterval(int visitingInterval) {
        this.visitingInterval = visitingInterval;
    }

    public ClientType getType() {
        return type;
    }
    public void setType(ClientType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Client other = (Client) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (visitingInterval != other.visitingInterval)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    
}

