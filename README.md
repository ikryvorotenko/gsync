# gsync
Git-svn syncronization tool

Thanks for the inspiration to https://github.com/mrts/git-svn-bridge. The original idea is really cool, but it's pretty complex to set up and the auth manager is Win based, so this project comes to resolve these issues and bring simple tool for svn-git two ways syncronization.

To run this:

```
docker run -d -p 9521:9521 \
 --name gsync \
 --volume /home/remote/git-sync:/etc/gsync \
 -e DEFAULT_SYNC_USER='kryvori' \
 -e DEFAULT_SYNC_PASSWORD='#kag8201' \
 -e GSYNC_KEY='NsA04KJDjNahYt62' \
 ikryvorotenko/gsync:0.1
 
```