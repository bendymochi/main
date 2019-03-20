package seedu.equipment.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static seedu.equipment.testutil.TypicalEquipments.ANCHORVALECC;
import static seedu.equipment.testutil.TypicalEquipments.HOON;
import static seedu.equipment.testutil.TypicalEquipments.IDA;
import static seedu.equipment.testutil.TypicalEquipments.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.equipment.commons.exceptions.DataConversionException;
import seedu.equipment.model.EquipmentManager;
import seedu.equipment.model.ReadOnlyEquipmentManager;
import seedu.equipment.storage.JsonEquipmentManagerStorage;

public class JsonEquipmentManagerStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "JsonEquipmentManagerStorageTest");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        readAddressBook(null);
    }

    private java.util.Optional<ReadOnlyEquipmentManager> readAddressBook(String filePath) throws Exception {
        return new JsonEquipmentManagerStorage(Paths.get(filePath))
                .readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() throws Exception {

        thrown.expect(DataConversionException.class);
        readAddressBook("notJsonFormatEquipmentManager.json");

        // IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
        // That means you should not have more than one exception test in one method
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readAddressBook("invalidEquipmentEM.json");
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readAddressBook("invalidAndValidEquipmentEM.json");
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.getRoot().toPath().resolve("TempAddressBook.json");
        EquipmentManager original = getTypicalAddressBook();
        JsonEquipmentManagerStorage jsonEquipmentManagerStorage = new JsonEquipmentManagerStorage(filePath);

        // Save in new file and read back
        jsonEquipmentManagerStorage.saveAddressBook(original, filePath);
        ReadOnlyEquipmentManager readBack = jsonEquipmentManagerStorage.readAddressBook(filePath).get();
        assertEquals(original, new EquipmentManager(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addPerson(HOON);
        original.removePerson(ANCHORVALECC);
        jsonEquipmentManagerStorage.saveAddressBook(original, filePath);
        readBack = jsonEquipmentManagerStorage.readAddressBook(filePath).get();
        assertEquals(original, new EquipmentManager(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonEquipmentManagerStorage.saveAddressBook(original); // file path not specified
        readBack = jsonEquipmentManagerStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new EquipmentManager(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveAddressBook(null, "SomeFile.json");
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyEquipmentManager addressBook, String filePath) {
        try {
            new JsonEquipmentManagerStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveAddressBook(new EquipmentManager(), null);
    }
}
