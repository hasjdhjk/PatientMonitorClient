package Models;

import java.util.ArrayList;
import java.util.List;

public class VitalRecordIO {

    private static final List<VitalRecord> records = new ArrayList<>();

    public static void append(VitalRecord r) {
        records.add(r);
    }

    public static List<VitalRecord> loadAll() {
        return records;
    }
}
