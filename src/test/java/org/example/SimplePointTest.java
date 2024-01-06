package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SimplePointTest {

    @Test
    public void testGetX() {
        SimplePoint point = new SimplePoint(5, 10);
        assertEquals(5, point.getX());
    }

    @Test
    public void testGetY() {
        SimplePoint point = new SimplePoint(5, 10);
        assertEquals(10, point.getY());
    }
    @Test
    public void testHashCode() {
        SimplePoint point1 = new SimplePoint(5, 10);
        SimplePoint point2 = new SimplePoint(5, 10);
        SimplePoint point3 = new SimplePoint(10, 5);

        // Azonos objektumoknak azonos hash kódjuk kell legyen
        assertEquals(point1.hashCode(), point2.hashCode());

        // Különböző objektumoknak általában különböző hash kódjuk van
        // Bár ez nem mindig garantált, ezért ezt a tesztet csak tájékoztató jelleggel használjuk
        assertNotEquals(point1.hashCode(), point3.hashCode());
    }
    @Test
    public void testSetX() {
        SimplePoint point = new SimplePoint(5, 10);
        point.setX(7);
        assertEquals(7, point.getX());
    }

    @Test
    public void testSetY() {
        SimplePoint point = new SimplePoint(5, 10);
        point.setY(8);
        assertEquals(8, point.getY());
    }
}
