# Quiz 1.docx

Let's break down the analysis of the question and its solution from Quiz 1 related to Object-Oriented Programming (OOP).

### Question Analysis
**Question**: Infer a Java program using OOP concepts for the following scenario, we have concrete classes for real animals like Cow, Lion, Parrot, and Elephant. Each class implements the Animal interface, providing implementations for the eat(), sleep(), and makeSound() methods. These methods define the common behaviours of all animals. The Mammal and Bird abstract classes provide common properties and behaviours for mammals and birds, respectively. The main class Zoo demonstrates the usage of these classes by creating instances of different animals and invoking their behaviours. 
- **CLO**: CLO-1
- **Bloom's Taxonomy Level**: C2 (Understanding)

**Identifying the Taxonomy Level**:
- The task requires the application of OOP concepts and understanding class hierarchies. It fits within the **Understanding (Level 2)** category of Bloom's Taxonomy since it necessitates comprehension of class relationships and the ability to infer a working code from specifications.

**Analysis**:
- **Matched Level**: Yes, the question aligns with the specified level of C2.

---

### Solution Analysis
**Solution**:
1. **Interface and Abstract Classes**:
   - **Animal Interface**: Declares common behaviors.
   - **Mammal and Bird Abstract Classes**: Provide properties/behaviors.

2. **Concrete Classes**:
   - **Implementations**: Cow, Lion, Elephant use Mammal; Parrot uses Bird.

3. **Main Class**:
   - The **Zoo** class demonstrates the usage by creating instances of these classes.

**Code Implementation**: 
The solution provides a complete Java code that satisfies the requirements of the question, showcasing the use of inheritance, interfaces, and polymorphism.

### Rating & Feedback
- **Rating**: 10/10
- **Key Elements**: 
  - Correct implementation of interfaces and abstract classes.
  - Demonstrates instance creation and method invocation as per OOP principles.
  
**Conclusion**:
- The solution effectively meets the expected level of understanding (C2) within Bloom's Taxonomy. There are no suggestions for improvements as it accurately reflects the requirements of the question.

Overall, both the question and its corresponding solution align well with the specified Bloom's Taxonomy level, presenting a coherent learning objective and appropriately challenging task for students.

# ---------------------------------------------------------------------




# Quiz 2.docx

### Question Analysis

1. **Question 1: Concatenating Strings**  
   **Given CLO & Level:** CLO-2, C3  
   **Taxonomy Level Determination:** C3 indicates "Applying". The task requires students to apply string manipulation skills to concatenate two strings.  
   **Analysis Outcome:** The question aligns with the specified Bloom's level.

2. **Question 2: Searching Substrings**  
   **Given CLO & Level:** CLO-2, C3  
   **Taxonomy Level Determination:** The task asks students to check for the presence of a substring within another string, which is a practical application of string functions.  
   **Analysis Outcome:** The question aligns with the specified Bloom's level.

3. **Question 3: Splitting Strings**  
   **Given CLO & Level:** CLO-2, C4  
   **Taxonomy Level Determination:** C4 indicates "Analyzing". The task involves splitting a sentence, which requires a higher level of understanding and manipulation of strings.  
   **Analysis Outcome:** The question is at C3 (Applying) but is specified for C4 (Analyzing).  
   **Suggestions for Improvement:**  
   - **Modify the question to require students to analyze the words split from the sentence, perhaps by counting words or identifying unique occurrences.**  
   - **Example Questions:**  
     - "Write a program that prompts the user to enter a sentence and then splits the sentence into words, counts the number of words, and prints the results."  
     - "Develop a program that splits a sentence into words and analyzes the frequency of each word, printing each unique word followed by its count."


### Solution Analysis

1. **Solution for Question 1**  
   **Rating:** 10/10  
   **Key Elements:** Correctly handles input and concatenates strings as required.  
   **Improvement:** None needed.

2. **Solution for Question 2**  
   **Rating:** 10/10  
   **Key Elements:** Correctly implements a method to check for substring presence and responds appropriately based on the result.  
   **Improvement:** None needed.

3. **Solution for Question 3**  
   **Rating:** 7/10  
   **Key Elements:** Correctly splits the sentence into words and prints them line by line.  
   **Suggestions for Improvement:** To meet the C4 level, the solution should include additional logic to analyze the number of words and/or their frequency.  
   **How to Enhance:**  
   - Include a mechanism to count the number of words and use a data structure like a HashMap to count occurrences of each word.
   - Update the solution code to reflect this added analysis.

### Summary

The first two questions align well with the specified Bloom's taxonomy levels, and their solutions are adequately executed. The third question requires revision to elevate it from a C3 to a C4 level, alongside enhancements in its corresponding solution to reflect a more analytical approach.

# ---------------------------------------------------------------------




# Quiz 3.docx

### Question Analysis

**Question:**
Create a java program to input a paragraph from the user and count the words “The” and “is” ignoring the case and write the count in a file.  
- **CLO:** CLO-3  
- **Bloom's Level:** C5 (Evaluate/Analyze)

**Taxonomy Level Identification:**
The command in the question requires the application and analysis of knowledge through the creation of a program which is consistent with the "C5" level of Bloom’s Taxonomy. 

**Comparison:**
The question meets the specified level as creating a program requires evaluation and analysis capabilities.

**Conclusion:** 
This question is aligned with the desired taxonomy level.

### Solution Analysis

**Solution:**
The provided Java code captures the user’s input, counts the occurrences of specified words ignoring cases, and writes the results to a file. 

1. **Rating:** 9/10  
   - The solution effectively captures the intent of the question and demonstrates a functional approach to word counting and file I/O.
  
2. **Key Elements:**
   - Utilizes a `WordCounter` class to encapsulate word counting logic.
   - Efficiently handles text by converting it to lowercase.
   - Demonstrates file writing using `BufferedWriter`.
   - Provides console output for user interaction.

3. **Suggestions for Improvement:**
   - **Enhancement in User Interaction:** Add error handling for the case when the user enters an empty paragraph.
   - **Example Improvement:** Modify the program to allow users to specify multiple words to count. This makes the solution more versatile and educational.
   
   **Improved Example:**
   ```java
   System.out.println("Enter words to count separated by commas:");
   String[] wordsToCount = scanner.nextLine().split(",");
   for (String word : wordsToCount) {
       int count = wordCounter.countWord(paragraph, word.trim());
       System.out.println("Count of '" + word.trim() + "': " + count);
   }
   ```

Overall, both the question and its solution align well with the specified Bloom's Taxonomy level, exhibiting clear educational value.

# ---------------------------------------------------------------------




