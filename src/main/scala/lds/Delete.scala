//package lds
//
//import com.typesafe.config.ConfigFactory
//import lds.model.WebHook
//import lds.model.WebHook.PayloadDetails
//import lds.services.WebhookSecretChecker
//import play.api.libs.json.Json
//
//object Delete {
//
//  val payload = """{
//                  |  "ref": "refs/heads/master",
//                  |  "before": "1883d16ff77a8e1f0e7fe752f03a4b2da89d7039",
//                  |  "after": "31b84a3b8b9245f8d3109178410297cccb3db7aa",
//                  |  "created": false,
//                  |  "deleted": false,
//                  |  "forced": false,
//                  |  "base_ref": null,
//                  |  "compare": "https://github.com/arminio/spike-LDS/compare/1883d16ff77a...31b84a3b8b92",
//                  |  "commits": [
//                  |    {
//                  |      "id": "31b84a3b8b9245f8d3109178410297cccb3db7aa",
//                  |      "tree_id": "9cb1db654c39d97ce1e54b09b6d3b25ed79a7693",
//                  |      "distinct": true,
//                  |      "message": "Update README.md",
//                  |      "timestamp": "2017-11-22T10:53:43Z",
//                  |      "url": "https://github.com/arminio/spike-LDS/commit/31b84a3b8b9245f8d3109178410297cccb3db7aa",
//                  |      "author": {
//                  |        "name": "Armin Keyvanloo",
//                  |        "email": "armin.keyvanloo@gmail.com",
//                  |        "username": "arminio"
//                  |      },
//                  |      "committer": {
//                  |        "name": "GitHub",
//                  |        "email": "noreply@github.com",
//                  |        "username": "web-flow"
//                  |      },
//                  |      "added": [
//                  |
//                  |      ],
//                  |      "removed": [
//                  |
//                  |      ],
//                  |      "modified": [
//                  |        "README.md"
//                  |      ]
//                  |    }
//                  |  ],
//                  |  "head_commit": {
//                  |    "id": "31b84a3b8b9245f8d3109178410297cccb3db7aa",
//                  |    "tree_id": "9cb1db654c39d97ce1e54b09b6d3b25ed79a7693",
//                  |    "distinct": true,
//                  |    "message": "Update README.md",
//                  |    "timestamp": "2017-11-22T10:53:43Z",
//                  |    "url": "https://github.com/arminio/spike-LDS/commit/31b84a3b8b9245f8d3109178410297cccb3db7aa",
//                  |    "author": {
//                  |      "name": "Armin Keyvanloo",
//                  |      "email": "armin.keyvanloo@gmail.com",
//                  |      "username": "arminio"
//                  |    },
//                  |    "committer": {
//                  |      "name": "GitHub",
//                  |      "email": "noreply@github.com",
//                  |      "username": "web-flow"
//                  |    },
//                  |    "added": [
//                  |
//                  |    ],
//                  |    "removed": [
//                  |
//                  |    ],
//                  |    "modified": [
//                  |      "README.md"
//                  |    ]
//                  |  },
//                  |  "repository": {
//                  |    "id": 111017861,
//                  |    "name": "spike-LDS",
//                  |    "full_name": "arminio/spike-LDS",
//                  |    "owner": {
//                  |      "name": "arminio",
//                  |      "email": "armin.keyvanloo@gmail.com",
//                  |      "login": "arminio",
//                  |      "id": 498781,
//                  |      "avatar_url": "https://avatars0.githubusercontent.com/u/498781?v=4",
//                  |      "gravatar_id": "",
//                  |      "url": "https://api.github.com/users/arminio",
//                  |      "html_url": "https://github.com/arminio",
//                  |      "followers_url": "https://api.github.com/users/arminio/followers",
//                  |      "following_url": "https://api.github.com/users/arminio/following{/other_user}",
//                  |      "gists_url": "https://api.github.com/users/arminio/gists{/gist_id}",
//                  |      "starred_url": "https://api.github.com/users/arminio/starred{/owner}{/repo}",
//                  |      "subscriptions_url": "https://api.github.com/users/arminio/subscriptions",
//                  |      "organizations_url": "https://api.github.com/users/arminio/orgs",
//                  |      "repos_url": "https://api.github.com/users/arminio/repos",
//                  |      "events_url": "https://api.github.com/users/arminio/events{/privacy}",
//                  |      "received_events_url": "https://api.github.com/users/arminio/received_events",
//                  |      "type": "User",
//                  |      "site_admin": false
//                  |    },
//                  |    "private": false,
//                  |    "html_url": "https://github.com/arminio/spike-LDS",
//                  |    "description": null,
//                  |    "fork": false,
//                  |    "url": "https://github.com/arminio/spike-LDS",
//                  |    "forks_url": "https://api.github.com/repos/arminio/spike-LDS/forks",
//                  |    "keys_url": "https://api.github.com/repos/arminio/spike-LDS/keys{/key_id}",
//                  |    "collaborators_url": "https://api.github.com/repos/arminio/spike-LDS/collaborators{/collaborator}",
//                  |    "teams_url": "https://api.github.com/repos/arminio/spike-LDS/teams",
//                  |    "hooks_url": "https://api.github.com/repos/arminio/spike-LDS/hooks",
//                  |    "issue_events_url": "https://api.github.com/repos/arminio/spike-LDS/issues/events{/number}",
//                  |    "events_url": "https://api.github.com/repos/arminio/spike-LDS/events",
//                  |    "assignees_url": "https://api.github.com/repos/arminio/spike-LDS/assignees{/user}",
//                  |    "branches_url": "https://api.github.com/repos/arminio/spike-LDS/branches{/branch}",
//                  |    "tags_url": "https://api.github.com/repos/arminio/spike-LDS/tags",
//                  |    "blobs_url": "https://api.github.com/repos/arminio/spike-LDS/git/blobs{/sha}",
//                  |    "git_tags_url": "https://api.github.com/repos/arminio/spike-LDS/git/tags{/sha}",
//                  |    "git_refs_url": "https://api.github.com/repos/arminio/spike-LDS/git/refs{/sha}",
//                  |    "trees_url": "https://api.github.com/repos/arminio/spike-LDS/git/trees{/sha}",
//                  |    "statuses_url": "https://api.github.com/repos/arminio/spike-LDS/statuses/{sha}",
//                  |    "languages_url": "https://api.github.com/repos/arminio/spike-LDS/languages",
//                  |    "stargazers_url": "https://api.github.com/repos/arminio/spike-LDS/stargazers",
//                  |    "contributors_url": "https://api.github.com/repos/arminio/spike-LDS/contributors",
//                  |    "subscribers_url": "https://api.github.com/repos/arminio/spike-LDS/subscribers",
//                  |    "subscription_url": "https://api.github.com/repos/arminio/spike-LDS/subscription",
//                  |    "commits_url": "https://api.github.com/repos/arminio/spike-LDS/commits{/sha}",
//                  |    "git_commits_url": "https://api.github.com/repos/arminio/spike-LDS/git/commits{/sha}",
//                  |    "comments_url": "https://api.github.com/repos/arminio/spike-LDS/comments{/number}",
//                  |    "issue_comment_url": "https://api.github.com/repos/arminio/spike-LDS/issues/comments{/number}",
//                  |    "contents_url": "https://api.github.com/repos/arminio/spike-LDS/contents/{+path}",
//                  |    "compare_url": "https://api.github.com/repos/arminio/spike-LDS/compare/{base}...{head}",
//                  |    "merges_url": "https://api.github.com/repos/arminio/spike-LDS/merges",
//                  |    "archive_url": "https://api.github.com/repos/arminio/spike-LDS/{archive_format}{/ref}",
//                  |    "downloads_url": "https://api.github.com/repos/arminio/spike-LDS/downloads",
//                  |    "issues_url": "https://api.github.com/repos/arminio/spike-LDS/issues{/number}",
//                  |    "pulls_url": "https://api.github.com/repos/arminio/spike-LDS/pulls{/number}",
//                  |    "milestones_url": "https://api.github.com/repos/arminio/spike-LDS/milestones{/number}",
//                  |    "notifications_url": "https://api.github.com/repos/arminio/spike-LDS/notifications{?since,all,participating}",
//                  |    "labels_url": "https://api.github.com/repos/arminio/spike-LDS/labels{/name}",
//                  |    "releases_url": "https://api.github.com/repos/arminio/spike-LDS/releases{/id}",
//                  |    "deployments_url": "https://api.github.com/repos/arminio/spike-LDS/deployments",
//                  |    "created_at": 1510863527,
//                  |    "updated_at": "2017-11-20T11:58:10Z",
//                  |    "pushed_at": 1511348024,
//                  |    "git_url": "git://github.com/arminio/spike-LDS.git",
//                  |    "ssh_url": "git@github.com:arminio/spike-LDS.git",
//                  |    "clone_url": "https://github.com/arminio/spike-LDS.git",
//                  |    "svn_url": "https://github.com/arminio/spike-LDS",
//                  |    "homepage": null,
//                  |    "size": 17,
//                  |    "stargazers_count": 0,
//                  |    "watchers_count": 0,
//                  |    "language": "Scala",
//                  |    "has_issues": true,
//                  |    "has_projects": true,
//                  |    "has_downloads": true,
//                  |    "has_wiki": true,
//                  |    "has_pages": false,
//                  |    "forks_count": 0,
//                  |    "mirror_url": null,
//                  |    "archived": false,
//                  |    "open_issues_count": 0,
//                  |    "forks": 0,
//                  |    "open_issues": 0,
//                  |    "watchers": 0,
//                  |    "default_branch": "master",
//                  |    "stargazers": 0,
//                  |    "master_branch": "master"
//                  |  },
//                  |  "pusher": {
//                  |    "name": "arminio",
//                  |    "email": "armin.keyvanloo@gmail.com"
//                  |  },
//                  |  "sender": {
//                  |    "login": "arminio",
//                  |    "id": 498781,
//                  |    "avatar_url": "https://avatars0.githubusercontent.com/u/498781?v=4",
//                  |    "gravatar_id": "",
//                  |    "url": "https://api.github.com/users/arminio",
//                  |    "html_url": "https://github.com/arminio",
//                  |    "followers_url": "https://api.github.com/users/arminio/followers",
//                  |    "following_url": "https://api.github.com/users/arminio/following{/other_user}",
//                  |    "gists_url": "https://api.github.com/users/arminio/gists{/gist_id}",
//                  |    "starred_url": "https://api.github.com/users/arminio/starred{/owner}{/repo}",
//                  |    "subscriptions_url": "https://api.github.com/users/arminio/subscriptions",
//                  |    "organizations_url": "https://api.github.com/users/arminio/orgs",
//                  |    "repos_url": "https://api.github.com/users/arminio/repos",
//                  |    "events_url": "https://api.github.com/users/arminio/events{/privacy}",
//                  |    "received_events_url": "https://api.github.com/users/arminio/received_events",
//                  |    "type": "User",
//                  |    "site_admin": false
//                  |  }
//                  |}
//                  |""".stripMargin
//
////  def main(args: Array[String]): Unit = {
////    val conf = ConfigFactory.load("code-inspection")
////
////    import pureconfig.syntax._
////
////    val rules = conf.to[AllRules]
////
////    println(rules)
////  }
////}
////
////final case class Rule(regex: String, tag: String)
////
////final case class AllRules(rules: List[Rule])
