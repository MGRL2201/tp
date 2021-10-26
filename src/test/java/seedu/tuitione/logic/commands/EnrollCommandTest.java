package seedu.tuitione.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.tuitione.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.tuitione.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.tuitione.testutil.TypicalIndexes.INDEX_FIRST_LESSON;
import static seedu.tuitione.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.tuitione.testutil.TypicalIndexes.INDEX_SECOND_LESSON;
import static seedu.tuitione.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.tuitione.testutil.TypicalIndexes.INDEX_THIRD_STUDENT;
import static seedu.tuitione.testutil.TypicalTuition.getTypicalTuitione;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.tuitione.commons.core.Messages;
import seedu.tuitione.commons.core.index.Index;
import seedu.tuitione.model.Model;
import seedu.tuitione.model.ModelManager;
import seedu.tuitione.model.UserPrefs;
import seedu.tuitione.model.lesson.Lesson;
import seedu.tuitione.model.lesson.LessonCode;
import seedu.tuitione.model.lesson.LessonTime;
import seedu.tuitione.model.lesson.Price;
import seedu.tuitione.model.lesson.Subject;
import seedu.tuitione.model.student.Grade;
import seedu.tuitione.model.student.Student;

public class EnrollCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalTuitione(), new UserPrefs());
    }

    @Test
    public void execute_validEnrollment_success() {
        EnrollCommand enrollCommand = new EnrollCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_LESSON);

        Model expectedModel = new ModelManager(getTypicalTuitione(), new UserPrefs());
        Student studentToEnroll = expectedModel.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());

        Lesson testLesson = expectedModel.getFilteredLessonList().get(INDEX_FIRST_LESSON.getZeroBased());
        testLesson.enrollStudent(studentToEnroll);
        String expectedMessage = String.format(EnrollCommand.MESSAGE_SUCCESS, studentToEnroll.getName(), testLesson);

        assertCommandSuccess(enrollCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidEnrollmentNotSameGrade_failure() {
        Lesson testLesson = model.getFilteredLessonList().get(INDEX_SECOND_LESSON.getZeroBased());
        Student alice = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased()); //ALICE
        Student carl = model.getFilteredStudentList().get(INDEX_THIRD_STUDENT.getZeroBased()); //CARL

        String expectedMessageAlice = String.format(EnrollCommand.MESSAGE_UNABLE_TO_ENROLL,
                alice.getName(),
                testLesson.getLessonCode());
        String expectedMessageCarl = String.format(EnrollCommand.MESSAGE_UNABLE_TO_ENROLL,
                carl.getName(),
                testLesson.getLessonCode());

        assertCommandFailure(new EnrollCommand(INDEX_FIRST_STUDENT, INDEX_SECOND_LESSON),
                model,
                expectedMessageAlice);
        assertCommandFailure(new EnrollCommand(INDEX_THIRD_STUDENT, INDEX_SECOND_LESSON),
                model,
                expectedMessageCarl);
    }

    @Test
    public void execute_invalidEnrollmentAlreadyInLesson_failure() {
        Lesson testLesson = model.getFilteredLessonList().get(INDEX_SECOND_LESSON.getZeroBased());
        LessonCode code = testLesson.getLessonCode();
        Student benson = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased()); //BENSON

        String expectedMessageBenson = String.format(EnrollCommand.MESSAGE_STUDENT_IN_LESSON,
                benson.getName(),
                code);

        assertCommandFailure(new EnrollCommand(INDEX_SECOND_STUDENT, INDEX_SECOND_LESSON),
                model,
                expectedMessageBenson);
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EnrollCommand invalidStudentEnrollment = new EnrollCommand(outOfBoundIndex, INDEX_SECOND_LESSON);
        EnrollCommand invalidLessonEnrollment = new EnrollCommand(INDEX_FIRST_STUDENT, outOfBoundIndex);
        EnrollCommand invalidIndexEnrollment = new EnrollCommand(outOfBoundIndex, outOfBoundIndex);

        assertCommandFailure(invalidStudentEnrollment, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        assertCommandFailure(invalidLessonEnrollment, model, Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertCommandFailure(invalidIndexEnrollment, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidLessonSizeMoreThan5_failure() {
        Student benson = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased()); //BENSON
        Lesson lessonTwo = new Lesson(new Subject("Science"),
                new Grade("S2"),
                new LessonTime(DayOfWeek.MONDAY, LocalTime.NOON),
                new Price(40));
        Lesson lessonThree = new Lesson(new Subject("SS"),
                new Grade("S2"),
                new LessonTime(DayOfWeek.TUESDAY, LocalTime.NOON),
                new Price(40));
        Lesson lessonFour = new Lesson(new Subject("Amath"),
                new Grade("S2"),
                new LessonTime(DayOfWeek.WEDNESDAY, LocalTime.NOON),
                new Price(40));
        Lesson lessonFive = new Lesson(new Subject("English"),
                new Grade("S2"),
                new LessonTime(DayOfWeek.THURSDAY, LocalTime.NOON),
                new Price(40));
        Lesson lessonSix = new Lesson(new Subject("Chinese"),
                new Grade("S2"),
                new LessonTime(DayOfWeek.FRIDAY, LocalTime.NOON),
                new Price(40));
        model.addLesson(lessonSix);
        benson.enrollForLesson(lessonTwo);
        benson.enrollForLesson(lessonThree);
        benson.enrollForLesson(lessonFour);
        benson.enrollForLesson(lessonFive);
        String expectedMessage = String.format(EnrollCommand.MESSAGE_MORE_THAN_5_LESSONS, benson.getName());
        assertCommandFailure(new EnrollCommand(INDEX_SECOND_STUDENT, Index.fromOneBased(4)),
                model,
                expectedMessage);
    }

    @Test
    public void equals() {
        EnrollCommand enrollFirstCommand = new EnrollCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_LESSON);
        EnrollCommand enrollSecondCommand = new EnrollCommand(INDEX_SECOND_STUDENT, INDEX_FIRST_LESSON);

        // same object -> returns true
        assertEquals(enrollFirstCommand, enrollFirstCommand);

        // same values -> returns true
        EnrollCommand enrollFirstCommandCopy = new EnrollCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_LESSON);
        assertEquals(enrollFirstCommand, enrollFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, enrollFirstCommand);

        // null -> returns false
        assertNotEquals(null, enrollFirstCommand);

        // different student -> returns false
        assertNotEquals(enrollFirstCommand, enrollSecondCommand);
    }
}
