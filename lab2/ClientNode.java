public class ClientNode extends Client implements GridNode{
    
    int row;
    int col;

    public ClientNode(String name, ClientType type, int start, int end) {
        super(name, type, start, end);
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public void setCol(int col) {
        this.col = col;
    }

}
