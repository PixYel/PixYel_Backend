/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.xml;

import pixyel_backend.xml.XML;
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
        toTest = XML.createNewXML(rootname);
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
        toTest.removeAttribute(attrName);
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
        toTest.removeContent();
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
        toTest.removeChildren(childname);
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
        toTest.removeChild(child);
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
        toTest.removeAttribute(test1[0]);
        toTest.removeAttributesByValue(test2[1]);
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
        toTest.removeAttribute(attrName);
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
        toTest.removeContent();
        assertFalse("Cannot restore original XML", toTest.hasContent());
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_0args() {
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", XML.createNewXML("B").getName(), toTest.getFirstChild().getName());
        toTest.removeChildren("A");
        toTest.removeChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    @Test
    public void testRemoveContent() {
        assertEquals("Shouldnt have content", "", toTest.getContent());
        String content = "testcontent";
        toTest.setContent(content);
        assertEquals("Should habe the same content", content, toTest.getContent());
        toTest.removeContent();
        assertFalse("Shouldnt have content", toTest.hasContent());
    }

    /**
     * Test of getLastChild method, of class XML.
     */
    @Test
    public void testGetLastChild() {//Sorts the children
        toTest.addChildren("B");
        toTest.addChildren("A");
        assertEquals("Should be equal", XML.createNewXML("A").getName(), toTest.getLastChild().getName());
        toTest.removeChildren("A");
        toTest.removeChildren("B");
        assertFalse("Cannot restore original XML", toTest.hasChildren());
    }

    /**
     * Test of getChildren method, of class XML.
     */
    @Test
    public void testGetChildren() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getChildren().get(0).getName(), "child");
        toTest.removeChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getChild method, of class XML.
     */
    @Test
    public void testGetChild() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getChild("child").get(0).getName(), "child");
        toTest.removeChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getFirstChild method, of class XML.
     */
    @Test
    public void testGetFirstChild_String() {
        toTest.addChildren("child");
        assertEquals("Should have the same name", toTest.getFirstChild("child").getName(), "child");
        toTest.removeChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of getParent method, of class XML.
     */
    @Test
    public void testGetParent() {
        assertNull("Shouldnt have a Parent", toTest.getParent());
        toTest.addChildren("child");
        assertEquals("Should have the same name", rootname, toTest.getFirstChild().getParent().getName());
        toTest.removeChildren("child");
        assertFalse("Could not restore the original XML", toTest.hasChildren());
    }

    /**
     * Test of addAttribute method, of class XML.
     */
    @Test
    public void testAddAttribute() {
        assertFalse("Shouldnt have a Attribute", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        assertTrue("Should have a Attribute", toTest.hasAttributes());
        toTest.removeAttribute("test");
        assertFalse("Could not recover the original XML", toTest.hasAttributes());
    }

    /**
     * Test of setContent method, of class XML.
     */
    @Test
    public void testSetContent() {
        assertEquals("Shouldnt have a content", "", toTest.getContent());
        toTest.setContent("testcontent");
        assertEquals("Should have the same content", toTest.getContent(), "testcontent");
        toTest.removeContent();
        assertEquals("Shouldnt have a content", "", toTest.getContent());
    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_XMLArr() {
        assertFalse("Should have no children", toTest.hasChildren());
        XML child = XML.createNewXML("child");
        toTest.addChildren(child);
        assertTrue("Should have a child", toTest.hasChildren());
        toTest.removeChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of addChildren method, of class XML.
     */
    @Test
    public void testAddChildren_StringArr() {
        assertFalse("Should have no children", toTest.hasChildren());
        toTest.addChildren("child");
        assertTrue("Should have a child", toTest.hasChildren());
        toTest.removeChildren("child");
        assertFalse("Should have no children", toTest.hasChildren());
    }

    /**
     * Test of RemoveAttribute method, of class XML.
     */
    @Test
    public void testRemoveAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.removeAttribute("test2");
        toTest.removeAttribute("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of removeAttributesByValue method, of class XML.
     */
    @Test
    public void testRemoveAttributesByValue() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.removeAttributesByValue("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of removeFirstAttributeByValue method, of class XML.
     */
    @Test
    public void testRemoveFirstAttributeByValue() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.removeFirstAttributeByValue("test");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.removeFirstAttributeByValue("test");
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of clearAttributes method, of class XML.
     */
    @Test
    public void testClearAttributes() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "bla");
        assertTrue("Should have Attributes", toTest.hasAttributes());
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    /**
     * Test of clearContent method, of class XML.
     */
    @Test
    public void testClearContent() {
        toTest.setContent("content");
        assertTrue("Should have a Content", toTest.hasContent());
        toTest.clearContent();
        assertFalse("Should have no Content", toTest.hasContent());
    }

    /**
     * Test of removeChildren method, of class XML.
     */
    @Test
    public void testRemoveChildren() {
        toTest.addChildren("child");
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.removeChildren("child");
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of removeChild method, of class XML.
     */
    @Test
    public void testRemoveChild() {
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        XML child = toTest.getFirstChild();
        toTest.removeChild(child);
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of clearChildren method, of class XML.
     */
    @Test
    public void testClearChildren() {
        toTest.addChildren("child1");
        toTest.addChildren("child2");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.clearChildren();
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of remove method, of class XML.
     */
    @Test
    public void testRemove() {
        toTest.addChildren("child");
        assertTrue("Should have Children", toTest.hasChildren());
        toTest.getChild("child").get(0).remove();
        assertFalse("Should have no Children", toTest.hasChildren());
    }

    /**
     * Test of isLastChild method, of class XML.
     */
    @Test
    public void testIsLastChild() {
        assertTrue("Should be the last child", toTest.isLastChild());
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_0args() {
        String expected = "<" + rootname + ">demo</" + rootname + ">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString());
        toTest.removeContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toXMLString method, of class XML.
     */
    @Test
    public void testToXMLString_boolean() {
        String expected = "<" + rootname + ">demo</" + rootname + ">";
        toTest.setContent("demo");
        assertEquals(expected, toTest.toXMLString(true));
        toTest.removeContent();
        assertFalse("Cannot restore original xml", toTest.hasContent());
    }

    /**
     * Test of toString method, of class XML.
     */
    @Test
    public void testToString() {
        toTest.addChildren("demo");
        toTest.setContent("content");
        toTest.getChild("demo").get(0).setContent("content2");
        String temp = toTest.toString();
        assertEquals("should be equal", "Name: \"Roottag\" ; Content: \"content\" ; Children: 1 \n"
                + "├─Name: \"demo\" ; Content: \"content2\" ; Parent: \"Roottag\" ", toTest.toString());
        toTest.removeChildren("demo");
        toTest.removeContent();

    }

    @Test
    public void testRemoveFirstChild() {
        toTest.addChildren("child2");
        toTest.addChildren("child1");
        toTest.removeFirstChild();
        assertEquals("Deletet wrong child", "child1", toTest.getFirstChild().getName());
        toTest.clearChildren();
        assertFalse("Could not restore the original xml", toTest.hasChildren());
    }

    @Test
    public void testRemoveLastChild() {
        toTest.addChildren("child2");
        toTest.addChildren("child1");
        toTest.removeLastChild();
        assertEquals("Deletet wrong child", "child2", toTest.getLastChild().getName());
        toTest.clearChildren();
        assertFalse("Could not restore the original xml", toTest.hasChildren());
    }

    @Test
    public void testRemoveFirstAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test2");
        toTest.removeFirstAttribute();
        assertEquals("Should have the same Attribute values", "test2", toTest.getAttributes().get("test2"));
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    @Test
    public void testLastRemoveAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test2");
        toTest.removeLastAttribute();
        assertEquals("Should have the same Attribute values", "test", toTest.getAttributes().get("test"));
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    @Test
    public void testGetFirstAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test2");
        assertEquals("Should have the same Attribute values", "test", toTest.getFirstAttribute());
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    @Test
    public void testGetLastAttribute() {
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
        toTest.addAttribute("test", "test");
        toTest.addAttribute("test2", "test2");
        assertEquals("Should have the same Attribute values", "test2", toTest.getLastAttribute());
        toTest.clearAttributes();
        assertFalse("Shouldnt have no Attributes", toTest.hasAttributes());
    }

    @Test
    public void testAddChild() {
        toTest.addChild("child");
        assertTrue("Should have a children", toTest.hasChildren());
        assertEquals("Should have another name", "child", toTest.getFirstChild().getName());
        toTest.clearChildren();
        assertFalse("Could not recover XML", toTest.hasChildren());

        XML child = XML.createNewXML("childxml");
        toTest.addChild(child);
        assertTrue("Should have a children", toTest.hasChildren());
        assertEquals("Should have another name", "childxml", toTest.getFirstChild().getName());
        toTest.clearChildren();
        assertFalse("Could not recover XML", toTest.hasChildren());
    }

}
