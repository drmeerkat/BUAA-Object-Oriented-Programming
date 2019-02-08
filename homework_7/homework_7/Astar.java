package homework_7;



//def a_star_search(graph, start, goal):
//    frontier = PriorityQueue()
//    frontier.put(start, 0)
//    came_from = {}
//    cost_so_far = {}
//    came_from[start] = None
//    cost_so_far[start] = 0
//    
//    while not frontier.empty():
//        current = frontier.get()
//        
//        if current == goal:
//            break
//        
//        for next in graph.neighbors(current):
//            new_cost = cost_so_far[current] + graph.cost(current, next)
//            if next not in cost_so_far or new_cost < cost_so_far[next]:
//                cost_so_far[next] = new_cost
//                priority = new_cost + heuristic(goal, next)
//                frontier.put(next, priority)
//                came_from[next] = current
//    
//    return came_from, cost_so_far








//public class PriorityQueueTutorial{  
//    
//    public static void main(String args[]){  
//        PriorityQueue<Student> queue = new PriorityQueue<Student>(11,  
//                new Comparator<Student>() {  
//                  public int compare(Student s1, Student s2) {  
//                    return s1.grade - s2.grade;  
//                  }  
//                });       
//              
//        for (int i = 1; i <= 100; i++) {  
//            queue.add(new Student("s" + i, (new Random().nextInt(1000))));  
//        }  
//        while (!queue.isEmpty()) {  
//              System.out.println(queue.poll().toString());  
//        }  
//    }  
//}  
//












