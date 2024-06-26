package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.DateUtil;
import seedu.address.logic.commands.LinkLoanCommand.LinkLoanDescriptor;
import seedu.address.model.person.exceptions.DuplicateLoanException;
import seedu.address.model.person.exceptions.LoanNotFoundException;

/**
 * Represents a list of loans in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class UniqueLoanList implements Iterable<Loan> {

    private static final String DATE_MESSAGE_CONSTRAINTS = "Dates must be in the format dd-MM-yyyy.";
    private static int nextLoanId = 1;

    private final ObservableList<Loan> internalList = FXCollections.observableArrayList();
    private final ObservableList<Loan> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent loan as the given argument.
     */
    public boolean contains(Loan toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds a loan to the list of loans.
     *
     * @param loan A valid loan.
     */
    public void addLoan(Loan loan) {
        requireNonNull(loan);
        internalList.add(loan);
        updateNextLoanId();
    }

    /**
     * Adds a loan to the list of loans.
     *
     * @param value A valid value.
     * @param startDate A valid start date.
     * @param returnDate A valid return date.
     */
    public Loan addLoan(BigDecimal value, Date startDate, Date returnDate, Person assignee) {
        Loan loan = new Loan(nextLoanId, value, startDate, returnDate, assignee);
        addLoan(loan);
        return loan;
    }

    /**
     * Adds a loan to the list of loans.
     *
     * @param loanDescription A valid LinkLoanDescriptor, which contains details about the loan to be added.
     */
    public Loan addLoan(LinkLoanDescriptor loanDescription, Person assignee) {
        BigDecimal value = loanDescription.getValue();
        Date startDate = loanDescription.getStartDate();
        Date returnDate = loanDescription.getReturnDate();
        return addLoan(value, startDate, returnDate, assignee);
    }

    /**
     * Adds a loan to the list of loans.
     *
     * @param value A valid value.
     * @param startDate A valid start date.
     * @param returnDate A valid return date.
     * @throws IllegalValueException If the date string is not in the correct format.
     */
    public void addLoan(BigDecimal value, String startDate, String returnDate, Person assignee)
            throws IllegalValueException {
        try {
            Date start = DateUtil.parse(startDate);
            Date end = DateUtil.parse(returnDate);
            addLoan(value, start, end, assignee);
        } catch (IllegalValueException e) {
            throw new IllegalValueException(UniqueLoanList.DATE_MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Removes a loan from the list of loans.
     *
     * @param toRemove A valid loan.
     */
    public void removeLoan(Loan toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new LoanNotFoundException();
        }
    }

    public void setPersons(UniqueLoanList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    public void setLoans(List<Loan> replacement) {
        requireNonNull(replacement);
        if (!loansAreUnique(replacement)) {
            throw new DuplicateLoanException();
        }
        internalList.setAll(replacement);
        for (Loan loan : replacement) {
            nextLoanId = Math.max(nextLoanId, loan.getId() + 1);
        }
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Loan> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Loan> iterator() {
        return internalList.iterator();
    }

    /**
     * @param idx A valid index.
     * @return The loan at the specified index.
     */
    public Loan getLoan(int idx) {
        return internalList.get(idx);
    }

    public Loan getLoanById(int id) {
        for (Loan loan : internalList) {
            if (loan.getId() == id) {
                return loan;
            }
        }
        return null;
    }

    /**
     * Marks a loan as returned.
     *
     * @param idx A valid index.
     */
    public void markLoanAsReturned(int idx) {
        internalList.get(idx).markAsReturned();
    }

    /**
     * Marks a loan as returned.
     *
     * @param id A valid id.
     */
    public void markLoanAsReturnedById(int id) {
        Loan loan = getLoanById(id);
        if (loan != null) {
            loan.markAsReturned();
        }
    }

    /**
     * Marks a loan as not returned.
     *
     * @param loanToMark A valid loan.
     */
    public void markLoan(Loan loanToMark) {
        int index = internalList.indexOf(loanToMark);

        if (index == -1) {
            throw new LoanNotFoundException();
        }

        loanToMark.markAsReturned();
        internalList.set(index, loanToMark);
    }

    /**
     * Marks a loan of the specified index as returned.
     */
    public void markLoan(int idx) {
        internalList.get(idx).markAsReturned();
    }

    /**
     * @return A list of loans.
     */
    public List<Loan> getLoanList() {
        return new ArrayList<>(internalList);
    }

    /**
     * @return The id of the next loan.
     */
    public int getNextLoanId() {
        return nextLoanId;
    }

    /**
     * Updates the id of the next loan.
     */
    public void updateNextLoanId() {
        nextLoanId++;
    }

    /**
     * Unmarks a loan.
     *
     * @param loanToUnmark A valid loan.
     */
    public void unmarkLoan(Loan loanToUnmark) {
        int index = internalList.indexOf(loanToUnmark);

        if (index == -1) {
            throw new LoanNotFoundException();
        }

        loanToUnmark.unmarkAsReturned();
        internalList.set(index, loanToUnmark);
    }

    /**
     * Marks a loan of the specified index as not returned.
     */
    public void unmarkLoan(int idx) {
        internalList.get(idx).unmarkAsReturned();
    }

    /**
     * @return The number of loans in the list.
     */
    public int size() {
        return internalList.size();
    }

    @Override
    public String toString() {
        String output = "Loans:\n";
        int idx = 1;
        for (Loan loan : internalList) {
            output += idx + ". " + loan.toString() + "\n";
            idx++;
        }
        return output;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueLoanList)) {
            return false;
        }

        UniqueLoanList otherUniqueLoanList = (UniqueLoanList) other;
        // create hashset of ids of loans in this and other
        HashSet<Integer> thisLoanIds = new HashSet<>();
        HashSet<Integer> otherLoanIds = new HashSet<>();
        for (Loan loan : internalList) {
            thisLoanIds.add(loan.getId());
        }
        for (Loan loan : otherUniqueLoanList.internalList) {
            otherLoanIds.add(loan.getId());
        }
        return thisLoanIds.equals(otherLoanIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalList, nextLoanId);
    }

    /**
     * Returns true if {@code persons} contains only unique persons.
     */
    private boolean loansAreUnique(List<Loan> loans) {
        for (int i = 0; i < loans.size() - 1; i++) {
            for (int j = i + 1; j < loans.size(); j++) {
                if (loans.get(i).equals(loans.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Removes all loans attached to a person.
     *
     * @param key A valid person.
     */
    public void removeLoansAttachedTo(Person key) {
        internalList.removeIf(loan -> loan.getAssignee().equals(key));
    }


    /**
     * Modifies the assignee of all loans attached to a person.
     *
     * @param target A valid person.
     * @param editedPerson A valid person.
     */
    public void modifyLoanAssignee(Person target, Person editedPerson) {
        for (Loan loan : internalList) {
            if (loan.getAssignee().equals(target)) {
                loan.setAssignee(editedPerson);
            }
        }

        // Just to update the list for GUI
        if (!internalList.isEmpty()) {
            internalList.set(0, internalList.get(0));
        }
    }

    /**
     * Returns the maximum loan value of all loans.
     *
     * @return The maximum loan value of all loans.
     */
    public BigDecimal getMaxLoanValue() {
        BigDecimal maxLoanValue = BigDecimal.ZERO;
        for (Loan loan : internalList) {
            if (loan.getValue().compareTo(maxLoanValue) > 0) {
                maxLoanValue = loan.getValue();
            }
        }
        return maxLoanValue;
    }

    /**
     * Returns the earliest return date of all loans.
     * The loan must not be overdue and must not have been returned.
     *
     * @return The earliest return date of all loans. Returns null if there are no loans that meet the criteria.
     */
    public Date getEarliestReturnDate() {
        Date earliestReturnDate = null;
        for (Loan loan : internalList) {
            if ((earliestReturnDate == null || loan.getReturnDate().before(earliestReturnDate))
                    && !loan.isOverdue() && !loan.isReturned()) {
                earliestReturnDate = loan.getReturnDate();
            }
        }
        return earliestReturnDate;
    }
}
