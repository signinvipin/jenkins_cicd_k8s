# CI/CD Workflow Overview

```plaintext
Developer pushes to GitHub (main)
            ↓
     GitHub webhook triggers Jenkins
            ↓
   Jenkins Pipeline Starts (CI/CD)

  [Clone Git] → [Run Tests] → [Build Docker Image] 
                                ↓
                         [Push to Registry]
                                ↓
                  [Deploy to K8s via YAML]
                                ↓
         [Staging] → [Approval] → [Production]
                                ↓
                App is live & monitored
