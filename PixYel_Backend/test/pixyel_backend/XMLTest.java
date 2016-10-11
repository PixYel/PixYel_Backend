/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author i01frajos445
 */
public class XMLTest {

    XML toTest;
    String rootname = "Roottag";

    public XMLTest() {
        toTest = new XML(rootname);
    }

    /**
     * Test of hasAttributes method, of class XML.
     */
    @Test
    public void testHasAttributes() {
        String attrName = "testAttribute";
        assertFalse("Shouldnt have Attributes", toTest.hasAttributes());
        toTest.addAttribute(attrName, "1");
        assertTrue("Should have a Attribute", toTest.hasAttributes());
        toTest.deleteAttribute(attrName);
        assertFalse("Shouldnt have Attributes", toTest.hasAttributes());
    }

    /**
     * Test of hasContent method, of class XML.
     */
    @Test
    public void testHasContent() {
        assertFalse("Should have no Content", toTest.hasContent());
        toTest.setContent("Testcontent");
        assertTrue("Should have Content", toTest.hasContent());
        toTest.deleteContent();
        assertFalse("Should have no Content", toTest.hasContent());
    }

    /**
     * Test of hasChildren method, of class XML.
     */
    @Test
    public void testHasChildren() {
        assertFalse("Shoudnt have Children", toTest.hasChildren());
        String childname = "testchildren";
        toTest.addChildren(childname);
        assertTrue("Should have children", toTest.hasChildren());
        toTest.deleteChildren(childname);
        assertFalse("Shoudnt have Children", toTest.hasChildren());
    }

    /**
     * Test of hasParent method, of class XML.
     */
    @Test
    public void testHasParent() {
        toTest.addChildren("child");
        XML child = toTest.getFirstChild();
        assertTrue("Should have children", child.hasParent());
        toTest.deleteChild(child);
        assertFalse("Should have no Parents", toTest.hasParent());
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getName method, of class XML.
     */
    @Test
    public void testGetName() {
        assertEquals("Should have to same name", rootname, toTest.getName());
    }

    /**
     * Test of getAttributes method, of class XML.
     */
    @Test
    public void testGetAttributes() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        String test1[] = {"test1", "1"};
        toTest.addAttribute(test1[0], test1[1]);
        map.put(test1[0], test1[1]);
        String test2[] = {"test2", "2"};
        toTest.addAttribute(test2[0], test2[1]);
        map.put(test2[0], test2[1]);
        assertEquals("Should have the same attributes", map, toTest.getAttributes());
        toTest.deleteAttribute(test1[0]);
        toTest.deleteAttributesByValue(test2[1]);
        assertFalse("Cannot restore original XML", toTest.hasAttributes());
    }

    /**
     * Test of getAttribute method, of class XML.
     */
    @Test
    public void testGetAttribute() {
        String attrName = "testattr";
        String attrValue = "1";
        toTest.addAttribute(attrName, attrValue);
        assertEquals("Should have the same Attribute value", attrValue, toTest.getAttribute(attrName));
        toTest.deleteAttribute(attrName);
        assertFalse("cannot restore original XML", toTest.hasAttributes());

    }

    /**
     * Test of getContent method, of class XML.
     */
    @Test
    public void testGetContent() {
        assertEquals("Shouldnt have content", "", toTest.getContent());
        String content = "testcontent";
        toTest.setContent(content);
        assertEquals("Should habe the same content", content, toTest.getContent());
        toTest.deleteContent();
        assertFalse("Cannot restore original XML", toTest.hasContent());
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_0args() {
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", new XML("B").getName(), toTest.getFirstChild().getName());
        toTest.deleteChildren("A");
        toTest.deleteChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getLastChild method, of class XML.
     */
    @Test
    public void testGetLastChild() {//Sorts the children
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", new XML("B").getName(), toTest.getFirstChild().getName());
        toTest.deleteChildren("A");
        toTest.deleteChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getChildren method, of class XML.
     */
    @Test
    public void testGetChildren() {
        System.out.println("getChildren");
        XML instance = null;
        ArrayList<XML> expResult = null;
        ArrayList<XML> result = instance.getChildren();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChild method, of class XML.
     */
    @Test
    public void testGetChild() {
        System.out.println("getChild");
        String name = "";
        XML instance = null;
        ArrayList<XML> expResult = null;
        ArrayList<XML> result = instance.getChild(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_String() {
        System.out.println("getFirstChild");
        String name = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.getFirstChild(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class XML.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        XML instance = null;
        XML expResult = null;
        XML result = instance.getParent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addAttribute method, of class XML.
     */
    @Test
    public void testAddAttribute() {
        System.out.println("addAttribute");
        String name = "";
        String value = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.addAttribute(name, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setContent method, of class XML.
     */
    @Test
    public void testSetContent() {
        System.out.println("setContent");
        String content = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.setContent(content);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_XMLArr() {
        System.out.println("addChildren");
        XML[] children = null;
        XML instance = null;
        XML expResult = null;
        XML result = instance.addChildren(children);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_StringArr() {
        System.out.println("addChildren");
        String[] children = null;
        XML instance = null;
        XML expResult = null;
        XML result = instance.addChildren(children);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteAttribute method, of class XML.
     */
    @Test
    public void testDeleteAttribute() {
        System.out.println("deleteAttribute");
        String name = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.deleteAttribute(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteAttributesByValue method, of class XML.
     */
    @Test
    public void testDeleteAttributesByValue() {
        System.out.println("deleteAttributesByValue");
        String value = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.deleteAttributesByValue(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteFirstAttributeByValue method, of class XML.
     */
    @Test
    public void testDeleteFirstAttributeByValue() {
        System.out.println("deleteFirstAttributeByValue");
        String value = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.deleteFirstAttributeByValue(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearAttributes method, of class XML.
     */
    @Test
    public void testClearAttributes() {
        System.out.println("clearAttributes");
        XML instance = null;
        XML expResult = null;
        XML result = instance.clearAttributes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearContent method, of class XML.
     */
    @Test
    public void testClearContent() {
        System.out.println("clearContent");
        XML instance = null;
        XML expResult = null;
        XML result = instance.clearContent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteChildren method, of class XML.
     */
    @Test
    public void testDeleteChildren() {
        System.out.println("deleteChildren");
        String name = "";
        XML instance = null;
        XML expResult = null;
        XML result = instance.deleteChildren(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteChild method, of class XML.
     */
    @Test
    public void testDeleteChild() {
        System.out.println("deleteChild");
        XML child = null;
        XML instance = null;
        XML expResult = null;
        XML result = instance.deleteChild(child);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearChildren method, of class XML.
     */
    @Test
    public void testClearChildren() {
        System.out.println("clearChildren");
        XML instance = null;
        XML expResult = null;
        XML result = instance.clearChildren();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class XML.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        XML instance = null;
        instance.delete();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFileToSaveIn method, of class XML.
     */
    @Test
    public void testSetFileToSaveIn() {
        System.out.println("setFileToSaveIn");
        File file = null;
        XML instance = null;
        instance.setFileToSaveIn(file);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class XML.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        XML instance = null;
        instance.save();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveTo method, of class XML.
     */
    @Test
    public void testSaveTo() {
        System.out.println("saveTo");
        File toSaveIn = null;
        XML instance = null;
        instance.saveTo(toSaveIn);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isLastChild method, of class XML.
     */
    @Test
    public void testIsLastChild() {
        System.out.println("isLastChild");
        XML instance = null;
        boolean expResult = false;
        boolean result = instance.isLastChild();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_0args() {
        String expected = "<"+rootname+">demo</"+rootname+">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString());
        toTest.deleteContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_boolean() {
        String expected = "<"+rootname+">demo</"+rootname+">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString(true));
        toTest.deleteContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toString method, of class XML.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        XML instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
