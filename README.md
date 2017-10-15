# Android mapping survey app docs

# Android mapping survey app docs

### StackFragmentManager
#### Description
This class is responsible for traversing the questions depending on the given child flow. It also takes care of poping fragments as described in the exit flow given by the server.

#### Implementation
```Java
void moveToNextQuestion(Question node, ArrayList<Option> selectedOptionses);
```
+ check and save singleOptionData
+ if no children or all children answered call popQuestion(node).
+ get and push the next singleQuestion

```Java
void popQuestion(Question singleQuestion);
```
+ if `strategy: 'questionReference'` recruse until the questionReference has unanswered children.
+ if `strategy: 'repeat'` pop to the nearest questionReference which has `strategy: 'select'` in child flow.
+ if no **strategy** provided, end of loop singleQuestion. Pop everything.

```Java
Question getNextQuestion(Question node);
```
+ get next singleQuestion in optionOptionData optionOptionData based on **child flow**
+ if answered
	+ recruse until an unanswered singleQuestion is found

```Java
void onError(int code, String msg);
```
+ pass errors to the caller

```Java
void addRootFragment(Question singleQuestion);
```
+ get the fragment with this root singleQuestion.
+ add to the stack and show in the UI.
