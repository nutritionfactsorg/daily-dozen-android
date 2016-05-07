Contributing to the Daily Dozen for Android
===========================================

We would love for you to contribute to our source code and to help make the Daily Dozen for Android even better!  Here are the guidelines we would like you to follow:

 - [Code of Conduct](#coc)
 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Commit Message Guidelines](#commit)

<a name="coc"></a> Code of Conduct
----------------------------------

Help us to keep the [Daily Dozen for Android][daily-dozen-android] open and inclusive.  Please read and follow our [Code of Conduct][coc]

<a name="question"></a> Have a Question or Problem?
---------------------------------------------------

If you have a question or issue with how to use the [Daily Dozen for Android][daily-dozen-android] app or for other support-related questions, please contact us by visiting the [NutritionFacts.org Help Desk on Zendesk][zendesk].
 
For development-related questions or problems, please join us on our Slack [#development][slack-dev] channel.  (To join, please email [Christi Richards][cremail] for an invitation.)

<a name="issue"></a> Found an Issue?
------------------------------------

If you find a bug in the source code, a mistake in the documentation, or a translation error, you can help by submitting it in [the issues][issues].  Even better, you can submit a [Pull Request][pr] with a fix for the issue.

**Please Note:** This repository is *only* for issues within the [Daily Dozen Android][daily-dozen-android] source code. Issues in other app components or the [Daily Dozen iOS][daily-dozen-ios] version should be reported in their respective repositories.

**Please see our [Submission Guidelines](#submit) below for more information**

<a name="feature"></a> Want a Feature?
--------------------------------------

You can request a new feature by submitting [an issue][issue].

* **Major Changes** that you wish to contribute to the project should be discussed first on our Slack [#development][slack-dev] channel so that we can better coordinate our efforts, prevent duplication of work, and help you to craft the change so that it is successfully accepted into the project.  This is especially useful when dealing with large UI changes within the application that may require approval of mockups and screen workflows to stay consistent within our brand.

* **Small Changes** can be crafted and submitted to the GitHub Repository as a [Pull Request][pr].

<a name="submit"></a> Submission Guidelines
-------------------------------------------

### Submitting an Issue

Before you submit your issue, please search the archive.  Your issue may have already been addressed.

If your issue appears to be bug, and it hasn't been reported, open a [new issue][issue]. Please help us to maximize the effort we can spend fixing issues and adding new features by not reporting duplicate issues.

When submitting an issue, providing the following information will increase the chances of your request being dealt with quickly:

* **Overview of the Issue** - if an error is being thrown, a stack trace or log helps (if available)
* **Motivation for or Use Case** - explain why this is a bug for you and what was expected
* **Android Version(s)** - current Android version
* **Device Model** - is it a problem with all devices or only a specific model?
* **Stock or Customized System** - provide details on system customization, if customized
* **Daily Dozen App Version:** - current app version
* **Steps to Reproduce the Issue** - provide an unambiguous set of steps with screenshots (if possible)
* **Related Issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be causing the problem (line of code or commit)

### Working on Your First Pull Request?

We want to foster a community of participation and learning, especially for people interested in committing to FOSS projects. Kent C. Dodds provides a great set of tutorials covering [How to Contribute to an Open Source Project on GitHub][contribute-os] geared toward submitting your first Pull Request.  Check it out and start contributing!  

If you have any questions, or don't know where to start - join us on our Slack [#development][slack-dev] channel.  (To join, please email [Christi Richards][cremail] for an invitation.)

### Submitting a Pull Request

**Before you submit your Pull Request consider the following guidelines:**

Search for an open or closed [Pull Request][pr] that relates to your submission.  You don't want to duplicate effort.

Please submit all pull requests to the [nutritionfactsorg/daily-dozen-android][daily-dozen-android] repository in the `develop` branch.

As you're working on bug-fixes or features, please break them out into their own feature branches and open the [Pull Request][pr] against your feature branch. It makes it much easier to decipher down the road, as you open multiple Pull Requests over time, and makes it much easier for to approve pull requests quickly.

If you don't have a feature in mind, but would like to contribute back to the project, check out the [open issues][issues] and see if there are any you can tackle.

If you have a feature in mind that hasn't been asked for in [Github Issues][issues], please [open an issue][issue] so that we can discuss how it should work so that it will benefit the entire community.

<a name="commit"></a> Git Commit Guidelines
-------------------------------------------

### Commit Message Format

#### Single Line Commit

Not every commit requires a subject, body, and footer. Sometimes a single line is fine, especially when the change is so simple that no further context is necessary. For example:

```Fix typo in README.md```

#### Subject and Body Commit

When a commit merits a bit of explanation and context, you need to write a **body** in addition to the **subject**, separated by a ```<BLANK LINE>```:

```
<subject>
<BLANK LINE>
<body>
```

#### Full-size Commit (Subject, Body, and Footer)

When a commit **closes an issue**, this should be referenced in the **footer**.

A full commit with a **subject**, a **body** and a **footer** should also have each part separated by a ```<BLANK LINE>```:

```
<subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

### Subject

* **Limit the Subject Line to 50 Characters** - Keeping subject lines at this length ensures that they are readable, and forces you to think for a moment about the most concise way to explain what's going on.  If you're having a hard time summarizing, you might be committing too many changes at once.

* **Capitalize the Subject Line**

* **Do Not End the Subject Line with a Period** - Trailing punctuation is unnecessary in subject lines. Space is precious when you're trying to keep them to 50 characters or less.

* **Use the Imperative Mood in the Subject Line** - Imperative mood means "spoken or written as if giving a command or instruction". When you write your commit messages in the imperative, you're following git's own built-in conventions. For example:

	* Update getting started documentation
	* Release version 1.0.0

	A properly formed git commit subject line should always be able to complete the following sentence:
	
	If applied, this commit will *your subject line here*
	
	For example:
	
	* If applied, this commit will *update getting started documentation*
	* If applied, this commit will *release version 1.0.0*

	**Referencing a Revert in the Subject**
	
	If the commit reverts a previous commit, it should begin with `Revert: `, followed by the header of the reverted commit. In 	the body it should say: `This reverts commit <hash>.`, where the hash is the SHA of the commit being reverted.

### Body

**Wrap the Body at 72 Characters**

**Use the Body to Explain What and Why** - Leave out details about *how* a change has been made. Code is generally self-explanatory in this regard and can be viewed in the diff. Focus on the reasons you made the change in the first place, the way things worked before the change (and what was wrong with that), the way they work now, and why you decided to solve it the way you did.

### Footer
The footer should reference GitHub issues that this commit **Closes**.

[daily-dozen-android]: https://github.com/nutritionfactsorg/daily-dozen-android "Daily Dozen for Android"
[daily-dozen-ios]: https://github.com/nutritionfactsorg/daily-dozen-ios "Daily Dozen for iOS"
[nutritionfacts.org]: http://nutritionfacts.org "NutritionFacts.org - The Latest in Nutrition Research"
[coc]: https://github.com/nutritionfactsorg/daily-dozen-android/blob/master/CODE_OF_CONDUCT.md "Code of Conduct"
[zendesk]: http://nutritionfacts.zendesk.com "NutritionFacts.org Help Desk"
[slack-dev]: https://nutritionfacts.slack.com/messages/development/ "#Development on Slack"
[issues]: https://github.com/nutritionfactsorg/daily-dozen-android/issues "Daily Dozen for Android Issues"
[issue]: https://github.com/nutritionfactsorg/daily-dozen-android/issues/new "Create an Issue"
[pr]: https://github.com/nutritionfactsorg/daily-dozen-android/pulls "Pull Requests"
[contribute-os]: https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github "How to Contribute to an Open Source Project on GitHub"
[cremail]: mailto:christi@nutritionfacts.org?subject=Slack%20#Development%20Invitation