class RoomException extends Exception {
    public RoomException(String message) {
        super(message);
    }
}

public class RoomParser {
    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseRoom(item);
                System.out.println("Parsed: " + item);
            } catch (RoomException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void parseRoom(String item) throws RoomException {
        if (item == null || item.trim().isEmpty()) {
            throw new RoomException("Invalid room data format");
        }
    }
}