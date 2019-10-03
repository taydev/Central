package dev.tay.central.filesystem;

import dev.tay.central.devices.computer.Computer;
import dev.tay.central.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileSystemElement {

    private String type;
    private String name;
    private Computer computer;
    private Object contents;
    private FileSystemElement parentElement;

    private FileSystemElement() {
    }

    /**
     * Basic FSE Constructor.
     * @param type String representing type of element. Either "file" or "dir".
     * @param name Name of element.
     * @param computer Computer the element belongs to.
     * @param parentElement Parent element
     */
    public FileSystemElement(@Nonnull String type, @Nonnull String name, @Nonnull Computer computer,
                             FileSystemElement parentElement) {
        this(type, name, computer, parentElement, null);
    }

    public FileSystemElement(@Nonnull String type, @Nonnull String name, @Nonnull Computer computer,
                             FileSystemElement parentElement, Object contents) {
        if (type.equalsIgnoreCase("dir") || type.equalsIgnoreCase("file")) {
            this.type = type;
            this.name = name;
            this.computer = computer;

            if (contents != null)
                this.contents = contents;
            else {
                if (this.type.equalsIgnoreCase("dir"))
                    this.contents = new CopyOnWriteArrayList<FileSystemElement>();
                else
                    this.contents = "";
            }

            this.parentElement = parentElement;
        } else {
            throw new IllegalArgumentException(
                String.format("Invalid element type '%s' provided in constructor for file '%s'",
                    type, name)
            );
        }
    }

    public static FileSystemElement generateRootDirectory(Computer computer) {
        FileSystemElement rootElement = new FileSystemElement("dir", "", computer, null);
        rootElement.setParentElement(rootElement);
        rootElement.createBlankDirectory("home");
        rootElement.createBlankDirectory("bin");

        FileSystemElement sysDirectory = new FileSystemElement("dir", "sys", computer, rootElement);
        sysDirectory.createFile("os.bin", FileConstants.OS_BIN.getContents());

        rootElement.addElement(sysDirectory);
        return rootElement;
    }

    /**
     * Gets the type of element. This should always either be "dir" or "file". If it's not, something's fucked.
     *
     * @return The type of the element.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the name of the element. If it is a directory, a trailing forward slash is added.
     *
     * @return The name of the element.
     */
    public String getName() {
        if (this.isDirectory())
            return this.name + "/";
        else
            return this.name;
    }

    /**
     * Gets the computer linked to the element.
     *
     * @return The element's computer.
     */
    public Computer getComputer() {
        return this.computer;
    }

    /**
     * Gets the current element's parent element.
     *
     * @return The element's parent element.
     */
    public FileSystemElement getParentElement() {
        return this.parentElement;
    }

    /**
     * Checks whether or not the element is a directory. If it is, returns the contents as a FileSystemElement list.
     *
     * @return The element contents as a FileSystemElement list.
     * @throws IllegalArgumentException if the element is a file.
     */
    public List<FileSystemElement> getDirContents() throws IllegalArgumentException {
        if (this.isDirectory()) {
            // I know, I know. Unchecked cast. I can't fix it, but it should ALWAYS be this.
            return (List<FileSystemElement>) this.contents;
        } else {
            throw new IllegalArgumentException("Attempted to get directory contents on file object.");
        }
    }

    /**
     * Checks whether or not the element is a file. If it is, returns the contents as a string.
     *
     * @return The element contents as a string.
     * @throws IllegalArgumentException if the element is a directory.
     */
    public String getFileContents() throws IllegalArgumentException {
        if (this.isFile()) {
            return (String) this.contents;
        } else {
            throw new IllegalArgumentException("Attempted to get file contents on directory object.");
        }
    }
    //endregion

    //region Setters

    /**
     * Sets the name of the current element. If an element by the same name already exists in the parent element, it
     * will return false. Returns true if the rename was successful.
     *
     * @param name The new name for the element.
     * @return Whether or not the rename was successful.
     */
    public boolean setName(String name) {
        if (this.getParentElement().getElement(name) == null) {
            this.name = name;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the contents of the FileSystemElement to the specified object.
     * <p>
     * If the element is a file, the parameter is expected to be a string. If the element is a directory, the parameter
     * is expected to be a List<FileSystemElement>.
     *
     * @param obj The new contents for the object.
     */
    public void setContents(Object obj) {
        if (isDirectory() && obj instanceof List && ReflectionUtils.getGenericFromList((List<?>) obj).equalsIgnoreCase("FileSystemElement")) {
            this.contents = obj;
        } else if (isFile() && obj instanceof String) {
            this.contents = obj;
        } else {
            throw new IllegalArgumentException(String.format("Cannot set contents of type %s to class %s", getType(),
                obj.getClass().getSimpleName()));
        }
    }

    /**
     * This is not to be used under ANY circumstances other than initial creation of the root directory, as the parent
     * element of the root directory must be the root directory. To move an element, see {@link
     * FileSystemElement#move(FileSystemElement)}.
     *
     * @param parentElement The new parent element for the current element.
     */
    public void setParentElement(FileSystemElement parentElement) {
        if (this.parentElement == null)
            this.parentElement = parentElement;
    }

    /**
     * Moves the current element to the new parent element. This is to be used instead of
     * {@link FileSystemElement#setParentElement(FileSystemElement)}.
     * @param parentElement The new parent element for the current element.
     */
    public void move(FileSystemElement parentElement) {
        if (parentElement.isDirectory()) {
            this.getParentElement().getDirContents().remove(this);
            parentElement.getDirContents().add(this);
            this.parentElement = parentElement;
        }
    }

    /**
     * "Deletes" an element. By that, I mean it removes itself from its parent element and sets all of its values to
     * null or 0. Closest I can get.
     */
    public void delete() {
        this.parentElement.getDirContents().remove(this);
        nullify();
    }

    /**
     * Gets an element by the provided name.
     * <p>
     * As this method uses {@link FileSystemElement#getDirContents()}, it will throw an IllegalArgumentException if it
     * is used on a file, as elements cannot exist within files.
     *
     * @param name The name of the element to get.
     * @return The element by the given name, or null if it does not exist.
     */
    public FileSystemElement getElement(String name) {
        if (this.isDirectory()) {
            for (FileSystemElement fse : this.getDirContents()) {
                if (fse.getName().equals(name)) {
                    return fse;
                }
            }
        } else {
            throw new IllegalArgumentException("Elements cannot exist within files, only directories.");
        }
        return null;
    }

    /**
     * Gets a file by the given name. Similar to {@link FileSystemElement#getElement(String)}, but has an additional
     * check to see if the retrieved element is a file.
     *
     * @param name The name of the file to retrieve.
     * @return The retrieved file, or null if it doesn't exist.
     */
    public FileSystemElement getFile(String name) {
        FileSystemElement fse = getElement(name);
        if (fse != null && fse.isFile())
            return fse;
        return null;
    }

    /**
     * Gets a directory by the given name. Similar to {@link FileSystemElement#getElement(String)}, but has an
     * additional check to see if the retrieved element is a directory.
     *
     * @param name The name of the directory to retrieve. If this does not have a trailing "/", one is appended.
     * @return The retrieved directory, or null if it doesn't exist.
     */
    public FileSystemElement getDirectory(String name) {
        if (!name.endsWith("/"))
            name = name + "/";
        FileSystemElement fse = getElement(name);
        if (fse != null && fse.isDirectory())
            return fse;
        return null;
    }

    /**
     * Gets an element by the path relative to the current element. If the path provided begins with "/", it will begin
     * from the root directory and find the element from there. If not, it will explore the current element to find the
     * element at the end of the path.
     *
     * @param path The path of the element, relative to the current element.
     * @return The element at the end of the path, potentially null if not found.
     */
    public FileSystemElement getElementByRelativePath(String path) {
        FileSystemElement currentElement = this;
        if (path.startsWith("/")) {
            currentElement = computer.getRootDirectory();
            path = path.replaceFirst("/", "");
        }
        while (path.length() > 0) {
            if (currentElement != null) {
                if (path.contains("/")) {
                    String pathSegment = path.substring(0, path.indexOf("/") + 1);
                    if (pathSegment.equals("../")) {
                        currentElement = currentElement.getParentElement();
                    } else {
                        currentElement = currentElement.getDirectory(pathSegment);
                    }
                    path = path.replaceFirst(pathSegment, "");
                } else {
                    currentElement = currentElement.getFile(path.replaceAll("/", ""));
                    path = "";
                }
            } else
                path = "";
        }
        return currentElement;
    }
    //endregion

    //region Checks

    /**
     * Gets whether or not the type of element is "dir", meaning it's a directory.
     *
     * @return Whether or not the element is a directory.
     */
    public boolean isDirectory() {
        return this.type.equalsIgnoreCase("dir");
    }

    /**
     * Gets whether or not the type of element is "file", meaning it's a file.
     *
     * @return Whether or not the element is a file.
     */
    public boolean isFile() {
        return this.type.equalsIgnoreCase("file");
    }

    /**
     * Creates a file with the supplied name and contents within the element.
     * <p>
     * As this method uses {@link FileSystemElement#getDirContents()}, it will throw an IllegalArgumentException if you
     * use this method on a file since you can't create a file within a file.
     *
     * @param name     The name of the new file.
     * @param contents The contents of the new file.
     */
    public void createFile(String name, String contents) {
        addElement(new FileSystemElement("file", name, this.computer, this, contents));
    }

    /**
     * Creates a blank directory with the supplied name.
     * <p>
     * As this method uses {@link FileSystemElement#getDirContents()}, it will throw an IllegalArgumentException if you
     * use this method on a file since you can't create a directory within a file.
     *
     * @param name The name to give to the new blank directory.
     */
    public void createBlankDirectory(String name) {
        addElement(new FileSystemElement("dir", name, this.computer, this));
    }

    /**
     * Adds a FileSystemElement to the contents.
     * <p>
     * This can be either a file or a directory, and is typically used to add elements that require additional
     * information (e.g. a directory with files in it, see {@link FileSystemElement#generateRootDirectory(Computer)} for
     * an example. This method also has duplicate protection, so attempting to add an element with a name identical to
     * an element that already exists within the directory will ignore the creation attempt.
     * <p>
     * As this method uses {@link FileSystemElement#getDirContents()}, it will throw an IllegalArgumentException if you
     * use this method on a file since you can't add other elements to a file. If you wish to modify a file's contents,
     * use {@link FileSystemElement#setContents(Object)}.
     *
     * @param element The element to add to
     */
    public void addElement(FileSystemElement element) {
        if (getFile(element.getName()) == null && getDirectory(element.getName()) == null)
            getDirContents().add(element);
    }

    private void nullify() {
        this.type = null;
        this.name = null;
        this.contents = null;
        this.computer = null;
        this.parentElement = null;
    }
}
