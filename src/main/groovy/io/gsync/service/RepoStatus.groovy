package io.gsync.service

class RepoStatus {
    String gitHash
    String svnRevision

    String gitHashRevision
    String svnRevisionHash

    List<String> gitExtra
    List<String> svnExtra

    RepoStatus(String gitHash, String svnRevision, String svnRevisionHash, String gitHashRevision, List<String> gitExtra, List<String> svnExtra) {
        this.gitHash = gitHash
        this.svnRevision = svnRevision
        this.svnRevisionHash = svnRevisionHash
        this.gitHashRevision = gitHashRevision
        this.gitExtra = gitExtra
        this.svnExtra = svnExtra
    }
}
