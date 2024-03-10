public class DepotNode extends Depot implements GridNode {

    int row;
    int col;

    public DepotNode(String name) {
        super(name);
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
