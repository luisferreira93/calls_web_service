Calls Web Service

Objective
The main objective was to implement a service to manage a specific resource: Calls. 
The Call resource represents a phone call between two numbers with the following attributes:
•	Caller Number: the phone number of the caller.
•	Callee Number: the phone number of the callee.
•	Start Timestamp: start timestamp of the call.
•	End Timestamp: end timestamp of the call.
•	Type: Inbound or Outbound
This challenge also has two components: a Web Service and a Client.

Web Service

This Web Service should be able to manage and persist the Call resource, providing the following operations:

Create Calls (one or more).
Delete Call.
Get all Calls using pagination and be able to filter by Type.
Get statistics (the response to this operation should have the values aggregate by day, returning all days with calls):
Total call duration by type.
Total number of calls.
Number of calls by Caller Number.
Number of calls by Callee Number.
Total call cost using the following rules:
Outbound calls cost 0.05 per minute after the first 5 minutes. The first 5 minutes cost 0.10.
Inbound calls are free.
To persist the calls you should use any database that you feel comfortable with.

Solution
This challenge was made in Quarkus. Since this is a recent and trending technology, I decided to try it. The architecture is a simple API webservice default architecture, with webservices, data transfer objects, entities.
The database used was PostgreSQL and there is a need to run a docker before running the project (it will be explained lately in this document).
Every dependency used was generated by Quarkus with a maven command.

The following endpoints were created:
- /create
    Parameters:
        - @RequestBody "calls": [
                    {
                        "callType": "INBOUND",
                        "calleeNumber": "916821260",
                        "callerNumber": "917101905",
                        "endTimestamp": "2019-12-17T11:00:00",
                        "startTimestamp": "2019-12-17T09:00:00"
                    },
                    {
                        "callType": "INBOUND",
                        "calleeNumber": "916821260",
                        "callerNumber": "917765432",
                        "endTimestamp": "2019-12-17T13:00:00",
                        "startTimestamp": "2019-12-17T12:30:00"
                    }
                ]

- /delete/{callId}
    Parameters:
        - @PathParam callId 

- /retrieve
    Parameters
        - @QueryParam callType 
        - @QueryParam limit 
        - @QueryParam offset

- /statistics
    Parameters
        - @QueryParam endDate, example = 2020-01-02
        - @QueryParam startDate, example = 2020-01-01

How-to-run
First, you need to run the database docker. Run the following command:
    docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name postgres-quarkus-hibernate-panache -e POSTGRES_USER=user -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=call -p 5432:5432 postgres:10.5

To run this project, just simply run the jar located in the target folder with the following command:
    java -jar calls-service-1.0.0-SNAPSHOT-runner.jar

The previous step was meant to be done in a docker container but due to lack of time, it was not possible.

Notes
- There is a file "data.json" to use as an example for the /create endpoint.
- The Google's code conventions were used.
- The dates are in ISO 8601 format.