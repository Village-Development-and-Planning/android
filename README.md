# Android mapping survey app docs

# Android mapping survey app docs

### StackFragmentManager
#### Description
This class is responsible for traversing the questions depending on the given child flow. It also takes care of poping fragments as described in the exit flow given by the server.

#### Implementation
```Java
void moveToNextQuestion(Question node, ArrayList<Option> selectedOptions);
```
+ check and save options
+ if no children or all children answered call popQuestion(node).
+ get and push the next question

```Java
void popQuestion(Question question);
```
+ if `strategy: 'parent'` recruse until the parent has unanswered children.
+ if `strategy: 'repeat'` pop to the nearest parent which has `strategy: 'select'` in child flow.
+ if no **strategy** provided, end of loop question. Pop everything.

```Java
Question getNextQuestion(Question node);
```
+ get next question in optionData optionData based on **child flow**
+ if answered
	+ recruse until an unanswered question is found

```Java
void onError(int code, String msg);
```
+ pass errors to the caller

```Java
void addRootFragment(Question question);
```
+ get the fragment with this root question.
+ add to the stack and show in the UI.
