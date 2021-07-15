pipeline
{
    agent any

    stages 
    {
        stage ('Build')
        {
            steps
            {
                echo "Build..."
            }
        }
        stage ('Test')
        {
            steps
            {
                echo "Test..."
            }
        }
        stage ('Publish')
        {
            steps
            {
                echo "Publish..."
            }
        }
        stage ('CommitInfraRepo')
        {
            steps
            {
                echo "Commit..."
            }
        }
    }
}