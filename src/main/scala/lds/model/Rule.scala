package lds.model

final case class Rule(regex: String, tag: String)

final case class AllRules(rules: List[Rule])