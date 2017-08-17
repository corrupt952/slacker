## Cofiguration
### Repository Configration

|Label|Description|Note|
|----:|----------:|---:|
|Webhook URL|Slack `Incomming Webhook URL`||
|channel|Slack channel or UserId|Format example: `#channel` or `@user`|
|Events|Set the want to notified events|`Commented` require `User Map Json`|
|Silent|Set the not want to notified patterns||
|User Map JSON|Set the JSON mapped Stash user and Slack user||

### User Map JSON fomrat
Example:
```
{
    "stash-bob": "bob",
    "stash-james": "james"
}
```
