package pl.surecase.eu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.daogenerator.DaoGenerator;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {

        List<SchemaVersion> versions = new ArrayList<SchemaVersion>();

        versions.add(new Version1(true));

        validateSchemas(versions);

        for (SchemaVersion version : versions) {
            // NB: Test output creates stubs, we have an established testing
            // standard which should be followed in preference to generating
            // these stubs.
            new DaoGenerator().generateAll(version.getSchema(), args[0]);
        }
    }

    public static void validateSchemas(List<SchemaVersion> versions) throws IllegalArgumentException {
        int numCurrent = 0;
        Set<Integer> versionNumbers = new HashSet<Integer>();

        for (SchemaVersion version : versions) {
            if (version.isCurrent()) {
                numCurrent++;
            }

            int versionNumber = version.getVersionNumber();
            if (versionNumbers.contains(versionNumber)) {
                throw new IllegalArgumentException("Unable to process schema versions, multiple instances with version number : "
                        + version.getVersionNumber());
            }
            versionNumbers.add(versionNumber);
        }

        if (numCurrent != 1) {
            throw new IllegalArgumentException("Unable to generate schema, exactly one schema marked as current is required.");
        }
    }
}
