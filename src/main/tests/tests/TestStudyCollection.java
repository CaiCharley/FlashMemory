package tests;

import model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestStudyCollection<M extends StudyMaterial> {
    StudyCollection<M> sc1;
    StudyCollection<M> sc2;

    @Test
    void testAdd() {

    }
}
