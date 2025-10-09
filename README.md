# Java Spring Boot Blog Platform w/ React Typescript frontend

## A fully functional blog platform in Java and Typescript that makes blog posts.

This project is an example that was built along with the help of the @devtiro community showing how to create a blog app with spring security. 
The platform's basic components have been completed which shows how to do the following:

* Run PostgreSQL database using Docker containers to store data
* Create a REST API to perform CRUD operations for a typical blog site
* Secure the platform using Spring Security practices

# How to install and use the project yourself

1. clone this project
2. clean & compile code with Maven
3. open docker desktop
4. enter in the terminal: 'docker-compose up'
5. add and use your PostgreSQL password
6. run BlogApplication file
7. run the frontend portion of this project: https://github.com/Sunbird99/frontendblog

## How to tweak this project

Since this is mostly a starter project, I'd encourage you to clone and rename your repo to your liking. 
There is a test user for building and testing purposes outlined in the SecurityConfig file:

email: user@test.com
username: Test User
password: password

## Find a bug?

If you found an issue or would like to submit an improvement to this project, please use the issues tab above. 
If you would like to submit a problem with a fix, please reference the issue you created.

## Known issues

The project is still ongoing currently. Some current issues:

1. need to manually refresh the website after logging in
2. implement dark mode
3. improve validation methods on the frontend
