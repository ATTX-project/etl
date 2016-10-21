#----------------------------------
# Testing Google search features
#----------------------------------
    
@ignore
Feature: Do Google search

   # A very simple scenario
   Scenario: Simple search
      Given use has navigated to https://www.github.com
      When search for "attx-project"
      Then there should be results 
   
