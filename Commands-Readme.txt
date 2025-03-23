* to execute and run test cases

  mvn clean install exec:java -Dexec.mainClass="mainapp.MyApp" -DskipTests=true

git config --global user.email ""
git config --global user.name ""
--------------------------------------------------------------
git branch feature-a
git checkout feature-a
echo "Some content for feature A" > feature-a.txt
git add feature-a.txt
git commit -m "Add feature-a.txt"
git branch feature-b
git checkout feature-b
echo "Some content for feature B" > feature-b.txt
git add feature-b.txt
git commit -m "Add feature-b.txt"
git checkout feature-a
git merge feature-b
git checkout feature-b
echo "Some updated content for feature B" > feature-b-updated.txt
git add feature-b-updated.txt
git commit -m "Update feature-b with new content"
git branch -m feature-b feature-b-renamed
git checkout feature-a
git merge feature-b-renamed
