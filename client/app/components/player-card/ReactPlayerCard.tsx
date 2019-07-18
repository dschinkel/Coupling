import * as React from 'react'
import Player from "../../../../common/Player";
import {MouseEventHandler} from "react";

// @ts-ignore
import {components} from 'client'

interface PlayerCardProps {
    player: Player,
    tribeId: string,
    disabled?: boolean,
    size?: number,
    pathSetter?: (string) => void,
    className?: string,
    onClick?: MouseEventHandler
}

export default function ReactPlayerCard(props: PlayerCardProps) {
    return <components.PlayerCard {...props}/>
}
