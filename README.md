Operating System Simulation

The objective of this project is to implement a basic interpreter that reads text files representing programs and executes their code. Each text file represents a program, and when it is read and executed, it becomes a process within the simulation.

The project includes the following key components:

Interpreter: The interpreter reads the text files containing the program code and executes it. It simulates the behavior of a real interpreter by interpreting the instructions and performing the necessary operations.

Memory Management: The simulation includes a memory system to store the processes. The memory management component is responsible for allocating and deallocating memory space for the processes. It ensures efficient memory utilization and handles memory-related operations.

Mutexes: Mutual exclusion is crucial in an operating system to prevent concurrent access to critical resources. The simulation includes mutexes to enforce mutual exclusion. These mutexes ensure that only one process can access a critical resource at a time, preventing conflicts and maintaining data integrity.

Scheduler: The scheduler is responsible for process scheduling in the simulation. It determines the order in which processes are executed based on various scheduling algorithms. The scheduler ensures fair and efficient utilization of system resources and manages the execution of processes.
